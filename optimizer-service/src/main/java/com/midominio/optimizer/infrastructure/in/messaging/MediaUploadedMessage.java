package com.midominio.optimizer.infrastructure.in.messaging;

import java.util.UUID;

public record MediaUploadedMessage(UUID fileId, UUID ownerId, String storagePath, String fileType) {}
