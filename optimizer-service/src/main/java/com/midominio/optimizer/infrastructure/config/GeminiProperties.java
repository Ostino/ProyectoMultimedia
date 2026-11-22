package com.midominio.optimizer.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

/**
 * Configuración del etiquetado automático con la API de Google Gemini.
 *
 * @param apiKey  se lee solo de la variable de entorno GEMINI_API_KEY; vacía = etiquetado desactivado
 * @param model   modelo a usar (variable GEMINI_MODEL)
 * @param baseUrl URL base de la API REST
 * @param timeout tiempo máximo de conexión y de respuesta
 * @param maxTags cantidad máxima de etiquetas por archivo
 */
@ConfigurationProperties(prefix = "gemini")
public record GeminiProperties(
        String apiKey,
        @DefaultValue("gemini-3.8-flash") String model,
        @DefaultValue("https://generativelanguage.googleapis.com/v1beta") String baseUrl,
        @DefaultValue("20s") Duration timeout,
        @DefaultValue("5") int maxTags) {

    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isBlank();
    }
}
