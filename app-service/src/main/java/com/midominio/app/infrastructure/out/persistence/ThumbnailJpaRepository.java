package com.midominio.app.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ThumbnailJpaRepository extends JpaRepository<ThumbnailJpaEntity, UUID> {
    List<ThumbnailJpaEntity> findByFileId(UUID fileId);
    Optional<ThumbnailJpaEntity> findByFileIdAndLabel(UUID fileId, String label);
}
