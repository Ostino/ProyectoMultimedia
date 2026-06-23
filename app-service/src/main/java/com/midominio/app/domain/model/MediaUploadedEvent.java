package com.midominio.app.domain.model;

import java.util.UUID;

public record MediaUploadedEvent(UUID fileId, UUID ownerId, String storagePath, FileType fileType) {}
