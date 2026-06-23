package com.midominio.app.application.service;

import com.midominio.app.domain.model.FileStatus;
import com.midominio.app.domain.port.in.HandleMediaFailedUseCase;
import com.midominio.app.domain.port.out.MediaFileRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class HandleMediaFailedService implements HandleMediaFailedUseCase {

    private final MediaFileRepository mediaFileRepository;

    public HandleMediaFailedService(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    @Override
    @Transactional
    public void handle(UUID fileId) {
        mediaFileRepository.updateStatus(fileId, FileStatus.FAILED);
    }
}
