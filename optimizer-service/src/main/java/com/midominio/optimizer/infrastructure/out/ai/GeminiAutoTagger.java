package com.midominio.optimizer.infrastructure.out.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.midominio.optimizer.domain.model.MediaFile;
import com.midominio.optimizer.domain.model.ProcessingResult;
import com.midominio.optimizer.domain.port.out.AutoTagger;
import com.midominio.optimizer.infrastructure.config.GeminiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Adaptador de salida: pide a Google Gemini etiquetas para un archivo, usando la
 * miniatura/portada/vista previa generada (si existe) y los metadatos extraídos.
 *
 * <p>La API key viaja en la cabecera {@code x-goog-api-key} (nunca en la URL) para
 * que no aparezca en logs ni en mensajes de error.
 */
@Component
public class GeminiAutoTagger implements AutoTagger {

    private static final Logger log = LoggerFactory.getLogger(GeminiAutoTagger.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** Derivados que sirven como imagen representativa, en orden de preferencia. */
    private static final List<String> PREVIEW_LABELS =
            List.of("thumb_512", "cover", "preview", "thumb_1024", "thumb_128");
    private static final long MAX_IMAGE_BYTES = 4 * 1024 * 1024;

    private final GeminiProperties props;
    private final RestClient restClient;

    public GeminiAutoTagger(GeminiProperties props) {
        this.props = props;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) props.timeout().toMillis());
        factory.setReadTimeout((int) props.timeout().toMillis());
        this.restClient = RestClient.builder()
                .baseUrl(props.baseUrl())
                .requestFactory(factory)
                .build();

        if (props.hasApiKey()) {
            log.info("Etiquetado con IA activo (modelo {})", props.model());
        } else {
            log.warn("GEMINI_API_KEY no está configurada: los archivos se procesarán sin etiquetas automáticas");
        }
    }

    @Override
    public List<String> suggestTags(MediaFile file, ProcessingResult result) {
        if (!props.hasApiKey()) {
            throw new IllegalStateException("GEMINI_API_KEY no está configurada");
        }

        JsonNode response = restClient.post()
                .uri("/models/{model}:generateContent", props.model())
                .header("x-goog-api-key", props.apiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(buildRequest(file, result))
                .retrieve()
                .body(JsonNode.class);

        return parseTags(response);
    }

    private Map<String, Object> buildRequest(MediaFile file, ProcessingResult result) {
        List<Map<String, Object>> parts = new ArrayList<>();
        parts.add(Map.of("text", prompt(file, result)));
        readPreviewImage(result).ifPresent(base64 ->
                parts.add(Map.of("inlineData", Map.of("mimeType", "image/jpeg", "data", base64))));

        Map<String, Object> request = new LinkedHashMap<>();
        request.put("contents", List.of(Map.of("role", "user", "parts", parts)));
        request.put("generationConfig", Map.of(
                "responseMimeType", "application/json",
                "temperature", 0.2));
        return request;
    }

    private String prompt(MediaFile file, ProcessingResult result) {
        String metadata;
        try {
            metadata = MAPPER.writeValueAsString(result.getMetadata());
        } catch (IOException e) {
            metadata = "{}";
        }
        return """
                Eres un asistente que clasifica archivos de una biblioteca multimedia.
                Tipo de archivo: %s
                Metadatos extraídos: %s
                Si se adjunta una imagen, es una miniatura, portada o primera página del archivo.

                Devuelve entre 1 y %d etiquetas cortas en español (una o dos palabras cada una,
                en minúsculas, sin el símbolo #) que describan el contenido.
                Responde SOLO con un arreglo JSON de strings, por ejemplo: ["playa", "atardecer"].
                """.formatted(file.getFileType(), metadata, props.maxTags());
    }

    private Optional<String> readPreviewImage(ProcessingResult result) {
        for (String label : PREVIEW_LABELS) {
            String path = result.getDerivatives().get(label);
            if (path == null) continue;
            try {
                Path p = Path.of(path);
                if (Files.isRegularFile(p) && Files.size(p) <= MAX_IMAGE_BYTES) {
                    return Optional.of(Base64.getEncoder().encodeToString(Files.readAllBytes(p)));
                }
            } catch (IOException e) {
                log.debug("No se pudo leer el derivado {} para Gemini: {}", path, e.getMessage());
            }
        }
        return Optional.empty();
    }

    private List<String> parseTags(JsonNode response) {
        if (response == null) throw new IllegalStateException("Respuesta vacía de Gemini");

        StringBuilder text = new StringBuilder();
        for (JsonNode part : response.path("candidates").path(0).path("content").path("parts")) {
            text.append(part.path("text").asText(""));
        }

        String raw = text.toString().trim();
        int start = raw.indexOf('[');
        int end   = raw.lastIndexOf(']');
        if (start < 0 || end <= start) {
            throw new IllegalStateException("Gemini no devolvió un arreglo JSON de etiquetas");
        }

        try {
            JsonNode array = MAPPER.readTree(raw.substring(start, end + 1));
            List<String> tags = new ArrayList<>();
            for (JsonNode node : array) {
                if (node.isTextual() && !node.asText().isBlank()) tags.add(node.asText().trim());
                if (tags.size() >= props.maxTags()) break;
            }
            return tags;
        } catch (IOException e) {
            throw new IllegalStateException("Respuesta de Gemini con JSON inválido", e);
        }
    }
}
