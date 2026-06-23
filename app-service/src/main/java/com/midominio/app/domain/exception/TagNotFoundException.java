package com.midominio.app.domain.exception;

import java.util.UUID;

public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(UUID id) {
        super("Tag not found: " + id);
    }
}
