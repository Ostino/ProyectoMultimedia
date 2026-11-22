package com.midominio.app.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

/**
 * Modo demo: borrado automático de los archivos subidos.
 *
 * @param enabled         activa la limpieza periódica
 * @param retention       cuánto vive un archivo (ej: 30m, 1h, 2h)
 * @param keepFilesBefore los archivos creados antes de esta fecha se conservan,
 *                        con zona horaria (ej: 2026-11-23T10:00:00-04:00); vacío = ninguno
 */
@ConfigurationProperties(prefix = "demo.cleanup")
public record DemoCleanupProperties(
        @DefaultValue("false") boolean enabled,
        @DefaultValue("1h") Duration retention,
        String keepFilesBefore) {
}
