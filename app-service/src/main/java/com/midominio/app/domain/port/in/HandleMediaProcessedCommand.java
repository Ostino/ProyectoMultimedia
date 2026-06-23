package com.midominio.app.domain.port.in;

import java.util.Map;
import java.util.UUID;

public record HandleMediaProcessedCommand(
        UUID fileId,
        Map<String, String> derivatives,
        Map<String, Object> rawMetadata) {}
