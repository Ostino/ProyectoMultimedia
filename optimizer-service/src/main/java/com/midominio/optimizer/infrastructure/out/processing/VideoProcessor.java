package com.midominio.optimizer.infrastructure.out.processing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.midominio.optimizer.domain.model.FileType;
import com.midominio.optimizer.domain.model.MediaFile;
import com.midominio.optimizer.domain.model.ProcessingResult;
import com.midominio.optimizer.domain.port.out.MediaProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class VideoProcessor implements MediaProcessor {

    private static final Logger log = LoggerFactory.getLogger(VideoProcessor.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean supports(FileType fileType) {
        return fileType == FileType.VIDEO;
    }

    @Override
    public ProcessingResult process(MediaFile file) {
        try {
            Path source   = Path.of(file.getStoragePath());
            Path coverDir = source.getParent().resolve("cover");
            Files.createDirectories(coverDir);

            Path cover = coverDir.resolve("cover.jpg");
            exec("ffmpeg", "-y", "-ss", "00:00:01", "-i", source.toString(),
                    "-frames:v", "1",
                    cover.toString());

            Map<String, String> derivatives = Map.of("cover", cover.toString());

            String probeJson = probe(source.toString());
            Map<String, Object> metadata = new LinkedHashMap<>();
            JsonNode root   = MAPPER.readTree(probeJson);
            JsonNode format = root.path("format");
            if (!format.isMissingNode()) {
                if (format.has("duration")) metadata.put("duration", Double.parseDouble(format.get("duration").asText()));
                if (format.has("bit_rate")) metadata.put("bitrate",  Long.parseLong(format.get("bit_rate").asText()));
            }
            for (JsonNode stream : root.path("streams")) {
                String codecType = stream.path("codec_type").asText();
                if ("video".equalsIgnoreCase(codecType)) {
                    if (stream.has("codec_name")) metadata.put("videoCodec", stream.get("codec_name").asText());
                    if (stream.has("width"))      metadata.put("width",      stream.get("width").asInt());
                    if (stream.has("height"))     metadata.put("height",     stream.get("height").asInt());
                } else if ("audio".equalsIgnoreCase(codecType)) {
                    if (stream.has("codec_name")) metadata.put("audioCodec", stream.get("codec_name").asText());
                }
            }

            return ProcessingResult.success(file.getId(), file.getOwnerId(), derivatives, metadata);

        } catch (Exception e) {
            log.error("Video processing failed for {}", file.getId(), e);
            return ProcessingResult.failure(file.getId(), file.getOwnerId(), e.getMessage());
        }
    }

    private void exec(String... cmd) throws Exception {
        Process p = new ProcessBuilder(cmd)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start();
        int code = p.waitFor();
        if (code != 0) throw new RuntimeException("ffmpeg exited with code " + code);
    }

    private String probe(String input) throws Exception {
        Process p = new ProcessBuilder("ffprobe", "-v", "quiet",
                "-print_format", "json", "-show_streams", "-show_format", input)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start();
        String json = new String(p.getInputStream().readAllBytes());
        p.waitFor();
        return json;
    }
}
