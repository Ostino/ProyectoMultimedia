package com.midominio.app.domain.port.out;

import com.midominio.app.domain.model.Metadata;

import java.util.Optional;
import java.util.UUID;

public interface MetadataRepository {
    void save(Metadata metadata);
    Optional<Metadata> findByFileId(UUID fileId);
}
