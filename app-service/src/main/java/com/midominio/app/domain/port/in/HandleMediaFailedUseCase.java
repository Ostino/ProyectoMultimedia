package com.midominio.app.domain.port.in;

import java.util.UUID;

public interface HandleMediaFailedUseCase {
    void handle(UUID fileId);
}
