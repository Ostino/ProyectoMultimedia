package com.midominio.app.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface TagJpaRepository extends JpaRepository<TagJpaEntity, UUID> {
    Optional<TagJpaEntity> findByIdAndOwnerId(UUID id, UUID ownerId);
    Optional<TagJpaEntity> findByNameAndOwnerId(String name, UUID ownerId);
    List<TagJpaEntity>     findByOwnerId(UUID ownerId);
    boolean                existsByNameAndOwnerId(String name, UUID ownerId);
}
