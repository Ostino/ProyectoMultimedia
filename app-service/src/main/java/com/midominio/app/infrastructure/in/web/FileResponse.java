package com.midominio.app.infrastructure.in.web;

import com.midominio.app.domain.model.FileStatus;
import com.midominio.app.domain.model.FileType;
import com.midominio.app.domain.model.MediaFile;

import java.time.LocalDateTime;
import java.util.UUID;

public record FileResponse(
        UUID id,
        String originalName,
        String mimeType,
        FileType fileType,
        long sizeBytes,
        UUID folderId,
        UUID ownerId,
        FileStatus status,
        LocalDateTime createdAt) {

    public static FileResponse from(MediaFile f) {
        return new FileResponse(f.getId(), f.getOriginalName(), f.getMimeType(),
                f.getFileType(), f.getSizeBytes(), f.getFolderId(),
                f.getOwnerId(), f.getStatus(), f.getCreatedAt());
    }
}
