package com.midominio.app.domain.port.out;

import java.util.UUID;

public interface StorageService {
    String store(UUID ownerId, UUID fileId, String extension, byte[] content);
    void deleteFileDirectory(String storagePath);
}
