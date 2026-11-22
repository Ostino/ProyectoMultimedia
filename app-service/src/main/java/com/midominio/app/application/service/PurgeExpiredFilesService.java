package com.midominio.app.application.service;

import com.midominio.app.domain.model.MediaFile;
import com.midominio.app.domain.port.in.PurgeExpiredFilesUseCase;
import com.midominio.app.domain.port.out.MediaFileRepository;
import com.midominio.app.domain.port.out.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PurgeExpiredFilesService implements PurgeExpiredFilesUseCase {

    private static final Logger log = LoggerFactory.getLogger(PurgeExpiredFilesService.class);

    private final MediaFileRepository mediaFileRepository;
    private final StorageService storageService;

    public PurgeExpiredFilesService(MediaFileRepository mediaFileRepository, StorageService storageService) {
        this.mediaFileRepository = mediaFileRepository;
        this.storageService = storageService;
    }

    @Override
    public int purge(LocalDateTime createdBefore, LocalDateTime protectedBefore) {
        int deleted = 0;

        for (MediaFile file : mediaFileRepository.findCreatedBefore(createdBefore)) {
            if (isProtected(file, protectedBefore)) continue;

            // Primero la fila: si falla, el disco no se toca y el archivo sigue
            // siendo consistente. Metadatos, miniaturas y etiquetas se borran
            // en cascada (ON DELETE CASCADE en las migraciones).
            try {
                mediaFileRepository.delete(file.getId());
            } catch (RuntimeException e) {
                log.warn("No se pudo borrar el archivo expirado {}: {}", file.getId(), e.getMessage());
                continue;
            }
            deleted++;

            try {
                storageService.deleteFileDirectory(file.getStoragePath());
            } catch (RuntimeException e) {
                log.warn("Archivo {} borrado de la base, pero sus datos quedaron en disco: {}",
                        file.getId(), e.getMessage());
            }
        }

        return deleted;
    }

    private static boolean isProtected(MediaFile file, LocalDateTime protectedBefore) {
        return protectedBefore != null && file.getCreatedAt().isBefore(protectedBefore);
    }
}
