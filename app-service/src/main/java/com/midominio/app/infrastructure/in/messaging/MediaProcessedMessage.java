package com.midominio.app.infrastructure.in.messaging;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record MediaProcessedMessage(
        UUID fileId,
        UUID ownerId,
        Map<String, String> derivatives,
        Map<String, Object> metadata,
        boolean success,
        String errorMessage,
        List<String> aiTags) {}
