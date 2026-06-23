package com.midominio.app.infrastructure.in.web;

import com.midominio.app.domain.model.FileStatus;
import com.midominio.app.domain.model.FileType;
import com.midominio.app.domain.model.MediaFile;

import java.time.LocalDateTime;
import java.util.UUID;

public record SearchResultResponse(
        UUID          id,
        String        originalName,
        FileType      fileType,
        String        mimeType,
        FileStatus    status,
        long          sizeBytes,
        UUID          folderId,
        LocalDateTime createdAt) {

    public static SearchResultResponse from(MediaFile f) {
        return new SearchResultResponse(
                f.getId(), f.getOriginalName(), f.getFileType(), f.getMimeType(),
                f.getStatus(), f.getSizeBytes(), f.getFolderId(), f.getCreatedAt());
    }
}
