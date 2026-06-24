package com.midominio.app.application.service;

import com.midominio.app.domain.exception.FileNotFoundException;
import com.midominio.app.domain.model.MediaFile;
import com.midominio.app.domain.port.in.DeleteFileUseCase;
import com.midominio.app.domain.port.out.MediaFileRepository;
import com.midominio.app.domain.port.out.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteFileService implements DeleteFileUseCase {

    private final MediaFileRepository mediaFileRepository;
    private final StorageService storageService;

    public DeleteFileService(MediaFileRepository mediaFileRepository, StorageService storageService) {
        this.mediaFileRepository = mediaFileRepository;
        this.storageService = storageService;
    }

    @Override
    @Transactional
    public void delete(UUID fileId, UUID ownerId) {
        MediaFile file = mediaFileRepository.findByIdAndOwnerId(fileId, ownerId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        storageService.deleteFileDirectory(file.getStoragePath());
        mediaFileRepository.delete(fileId);
    }
}
