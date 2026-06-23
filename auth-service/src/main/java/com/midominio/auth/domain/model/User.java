package com.midominio.auth.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final LocalDateTime createdAt;

    public User(UUID id, String email, String passwordHash, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public static User create(String email, String passwordHash) {
        return new User(UUID.randomUUID(), email, passwordHash, LocalDateTime.now());
    }

    public UUID getId()             { return id; }
    public String getEmail()        { return email; }
    public String getPasswordHash() { return passwordHash; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
