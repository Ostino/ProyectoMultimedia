package com.midominio.app.application.service;

import com.midominio.app.domain.model.FileType;
import com.midominio.app.domain.model.MediaFile;
import com.midominio.app.domain.model.MediaUploadedEvent;
import com.midominio.app.domain.port.in.UploadFileCommand;
import com.midominio.app.domain.port.in.UploadFileUseCase;
import com.midominio.app.domain.port.out.MediaEventPublisher;
import com.midominio.app.domain.port.out.MediaFileRepository;
import com.midominio.app.domain.port.out.MimeTypeDetector;
import com.midominio.app.domain.port.out.StorageService;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class UploadFileService implements UploadFileUseCase {

    private final MediaFileRepository mediaFileRepository;
    private final StorageService storageService;
    private final MimeTypeDetector mimeTypeDetector;
    private final MediaEventPublisher mediaEventPublisher;

    public UploadFileService(MediaFileRepository mediaFileRepository,
                             StorageService storageService,
                             MimeTypeDetector mimeTypeDetector,
                             MediaEventPublisher mediaEventPublisher) {
        this.mediaFileRepository = mediaFileRepository;
        this.storageService      = storageService;
        this.mimeTypeDetector    = mimeTypeDetector;
        this.mediaEventPublisher = mediaEventPublisher;
    }

    @Override
    @Transactional
    public MediaFile upload(UploadFileCommand cmd) {
        String mimeType = mimeTypeDetector.detect(cmd.content(), cmd.originalName());
        FileType fileType = FileType.fromMimeType(mimeType);

        UUID fileId = UUID.randomUUID();
        String ext = extractExtension(cmd.originalName());
        String storagePath = storageService.store(cmd.ownerId(), fileId, ext, cmd.content());

        MediaFile mediaFile = MediaFile.createProcessing(
                fileId, cmd.originalName(), storagePath, mimeType,
                fileType, cmd.sizeBytes(), cmd.folderId(), cmd.ownerId());

        MediaFile saved = mediaFileRepository.save(mediaFile);

        mediaEventPublisher.publishUploaded(
                new MediaUploadedEvent(fileId, cmd.ownerId(), storagePath, fileType));

        return saved;
    }

    private String extractExtension(String filename) {
        if (filename == null) return "bin";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1).toLowerCase() : "bin";
    }
}
