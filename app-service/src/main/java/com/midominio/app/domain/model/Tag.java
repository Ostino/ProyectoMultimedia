package com.midominio.app.domain.model;

import java.util.UUID;

public class Tag {

    private final UUID   id;
    private final String name;
    private final UUID   ownerId;

    public Tag(UUID id, String name, UUID ownerId) {
        this.id      = id;
        this.name    = name;
        this.ownerId = ownerId;
    }

    public static Tag create(String name, UUID ownerId) {
        return new Tag(UUID.randomUUID(), name, ownerId);
    }

    public UUID   getId()      { return id; }
    public String getName()    { return name; }
    public UUID   getOwnerId() { return ownerId; }
}
