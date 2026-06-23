package com.midominio.app.application.service;

import com.midominio.app.domain.exception.FileNotFoundException;
import com.midominio.app.domain.model.FileDetail;
import com.midominio.app.domain.port.in.FileQueryUseCase;
import com.midominio.app.domain.port.out.MediaFileRepository;
import com.midominio.app.domain.port.out.MetadataRepository;
import com.midominio.app.domain.port.out.ThumbnailRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class FileQueryService implements FileQueryUseCase {

    private final MediaFileRepository mediaFileRepository;
    private final MetadataRepository  metadataRepository;
    private final ThumbnailRepository thumbnailRepository;

    public FileQueryService(MediaFileRepository mediaFileRepository,
                            MetadataRepository metadataRepository,
                            ThumbnailRepository thumbnailRepository) {
        this.mediaFileRepository = mediaFileRepository;
        this.metadataRepository  = metadataRepository;
        this.thumbnailRepository = thumbnailRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public FileDetail getDetail(UUID fileId, UUID ownerId) {
        var file = mediaFileRepository.findByIdAndOwnerId(fileId, ownerId)
                .orElseThrow(() -> new FileNotFoundException(fileId));
        var metadata    = metadataRepository.findByFileId(fileId);
        var derivatives = thumbnailRepository.findByFileId(fileId);
        return new FileDetail(file, metadata, derivatives);
    }
}
