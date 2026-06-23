package com.midominio.app.infrastructure.out.persistence;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "folders")
class FolderJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected FolderJpaEntity() {}

    FolderJpaEntity(UUID id, String name, UUID parentId, UUID ownerId, LocalDateTime createdAt) {
        this.id        = id;
        this.name      = name;
        this.parentId  = parentId;
        this.ownerId   = ownerId;
        this.createdAt = createdAt;
    }

    UUID getId()              { return id; }
    String getName()          { return name; }
    UUID getParentId()        { return parentId; }
    UUID getOwnerId()         { return ownerId; }
    LocalDateTime getCreatedAt() { return createdAt; }
}
