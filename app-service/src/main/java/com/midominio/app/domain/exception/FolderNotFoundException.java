package com.midominio.app.domain.exception;

import java.util.UUID;

public class FolderNotFoundException extends RuntimeException {
    public FolderNotFoundException(UUID id) {
        super("Folder not found: " + id);
    }
}
