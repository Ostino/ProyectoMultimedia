package com.midominio.app.infrastructure.in.scheduler;

import com.midominio.app.domain.port.in.PurgeExpiredFilesUseCase;
import com.midominio.app.infrastructure.config.DemoCleanupProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * Adaptador de entrada: dispara la limpieza de la demo cada cierto tiempo.
 * Solo existe si demo.cleanup.enabled=true.
 */
@Component
@ConditionalOnProperty(prefix = "demo.cleanup", name = "enabled", havingValue = "true")
public class DemoCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(DemoCleanupScheduler.class);

    private final PurgeExpiredFilesUseCase purgeUseCase;
    private final Duration retention;
    private final LocalDateTime protectedBefore;

    public DemoCleanupScheduler(PurgeExpiredFilesUseCase purgeUseCase, DemoCleanupProperties props) {
        this.purgeUseCase = purgeUseCase;
        this.retention = props.retention();
        this.protectedBefore = toLocalTime(props.keepFilesBefore());
        log.info("Modo demo activo: los archivos se borran después de {}{}", retention,
                protectedBefore == null ? "" : " (se conservan los creados antes de " + protectedBefore + ")");
    }

    @Scheduled(fixedDelayString = "${demo.cleanup.interval:PT5M}", initialDelayString = "PT1M")
    public void purgeExpiredFiles() {
        // created_at se guarda con LocalDateTime.now(), así que se compara en la misma zona
        LocalDateTime cutoff = LocalDateTime.now().minus(retention);
        int deleted = purgeUseCase.purge(cutoff, protectedBefore);
        if (deleted > 0) {
            log.info("Limpieza de demo: {} archivo(s) eliminado(s)", deleted);
        }
    }

    private static LocalDateTime toLocalTime(String value) {
        if (value == null || value.isBlank()) return null;
        return OffsetDateTime.parse(value.trim())
                .atZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
