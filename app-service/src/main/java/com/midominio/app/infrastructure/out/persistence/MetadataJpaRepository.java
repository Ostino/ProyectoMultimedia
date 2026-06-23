package com.midominio.app.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface MetadataJpaRepository extends JpaRepository<MetadataJpaEntity, UUID> {
    Optional<MetadataJpaEntity> findByFileId(UUID fileId);
}
