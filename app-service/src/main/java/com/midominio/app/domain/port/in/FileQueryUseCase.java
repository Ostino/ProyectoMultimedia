package com.midominio.app.domain.port.in;

import com.midominio.app.domain.model.FileDetail;

import java.util.UUID;

public interface FileQueryUseCase {
    FileDetail getDetail(UUID fileId, UUID ownerId);
}
