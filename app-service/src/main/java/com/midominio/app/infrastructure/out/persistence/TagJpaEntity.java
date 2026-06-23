package com.midominio.app.infrastructure.out.persistence;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tags")
class TagJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    protected TagJpaEntity() {}

    TagJpaEntity(UUID id, String name, UUID ownerId) {
        this.id      = id;
        this.name    = name;
        this.ownerId = ownerId;
    }

    UUID   getId()      { return id; }
    String getName()    { return name; }
    UUID   getOwnerId() { return ownerId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagJpaEntity other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }
}
