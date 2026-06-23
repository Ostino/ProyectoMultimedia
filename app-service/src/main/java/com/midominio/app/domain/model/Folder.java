package com.midominio.app.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Folder {

    private final UUID id;
    private final String name;
    private final UUID parentId;
    private final UUID ownerId;
    private final LocalDateTime createdAt;

    public Folder(UUID id, String name, UUID parentId, UUID ownerId, LocalDateTime createdAt) {
        this.id        = id;
        this.name      = name;
        this.parentId  = parentId;
        this.ownerId   = ownerId;
        this.createdAt = createdAt;
    }

    public static Folder create(String name, UUID parentId, UUID ownerId) {
        return new Folder(UUID.randomUUID(), name, parentId, ownerId, LocalDateTime.now());
    }

    public Folder rename(String newName) {
        return new Folder(id, newName, parentId, ownerId, createdAt);
    }

    public Folder move(UUID newParentId) {
        return new Folder(id, name, newParentId, ownerId, createdAt);
    }

    public UUID getId()              { return id; }
    public String getName()          { return name; }
    public UUID getParentId()        { return parentId; }
    public UUID getOwnerId()         { return ownerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
