package com.midominio.app.infrastructure.in.web;

import com.midominio.app.domain.model.Folder;

import java.time.LocalDateTime;
import java.util.UUID;

public record FolderResponse(UUID id, String name, UUID parentId, UUID ownerId, LocalDateTime createdAt) {

    public static FolderResponse from(Folder folder) {
        return new FolderResponse(
                folder.getId(),
                folder.getName(),
                folder.getParentId(),
                folder.getOwnerId(),
                folder.getCreatedAt());
    }
}
