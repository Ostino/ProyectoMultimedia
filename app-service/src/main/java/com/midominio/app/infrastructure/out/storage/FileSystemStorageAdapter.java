package com.midominio.app.infrastructure.out.storage;

import com.midominio.app.domain.port.out.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class FileSystemStorageAdapter implements StorageService {

    private final Path basePath;

    public FileSystemStorageAdapter(@Value("${storage.base-path:/data}") String basePath) {
        this.basePath = Path.of(basePath);
    }

    @Override
    public String store(UUID ownerId, UUID fileId, String extension, byte[] content) {
        try {
            Path dir = basePath.resolve(ownerId.toString()).resolve(fileId.toString());
            Files.createDirectories(dir);
            String filename = "original." + extension;
            Path target = dir.resolve(filename);
            Files.write(target, content);
            return target.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + fileId, e);
        }
    }
}
