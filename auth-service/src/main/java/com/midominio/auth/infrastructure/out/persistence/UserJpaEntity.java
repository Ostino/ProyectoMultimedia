package com.midominio.auth.infrastructure.out.persistence;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
class UserJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected UserJpaEntity() {}

    UserJpaEntity(UUID id, String email, String passwordHash, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    UUID getId()             { return id; }
    String getEmail()        { return email; }
    String getPasswordHash() { return passwordHash; }
    LocalDateTime getCreatedAt() { return createdAt; }
}
