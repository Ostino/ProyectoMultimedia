package com.midominio.app.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

interface MediaFileJpaRepository extends JpaRepository<MediaFileJpaEntity, UUID>,
                                          JpaSpecificationExecutor<MediaFileJpaEntity> {
    Optional<MediaFileJpaEntity> findByIdAndOwnerId(UUID id, UUID ownerId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE MediaFileJpaEntity e SET e.status = :status WHERE e.id = :id")
    void updateStatus(@org.springframework.data.repository.query.Param("id") UUID id,
                      @org.springframework.data.repository.query.Param("status") com.midominio.app.domain.model.FileStatus status);
}
