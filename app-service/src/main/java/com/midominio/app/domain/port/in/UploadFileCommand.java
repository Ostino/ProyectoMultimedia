package com.midominio.app.domain.port.in;

import java.util.UUID;

public record UploadFileCommand(String originalName, byte[] content, UUID folderId, UUID ownerId) {
    public long sizeBytes() { return content.length; }
}
