package com.midominio.app.domain.port.in;

import java.util.UUID;

public interface DeleteFileUseCase {
    void delete(UUID fileId, UUID ownerId);
}
