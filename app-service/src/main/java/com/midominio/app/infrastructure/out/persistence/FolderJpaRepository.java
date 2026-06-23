package com.midominio.app.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface FolderJpaRepository extends JpaRepository<FolderJpaEntity, UUID> {
    Optional<FolderJpaEntity> findByIdAndOwnerId(UUID id, UUID ownerId);
    List<FolderJpaEntity> findByParentIdAndOwnerId(UUID parentId, UUID ownerId);
    List<FolderJpaEntity> findByParentIdIsNullAndOwnerId(UUID ownerId);
}
