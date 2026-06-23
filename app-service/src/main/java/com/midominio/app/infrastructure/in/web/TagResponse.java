package com.midominio.app.infrastructure.in.web;

import com.midominio.app.domain.model.Tag;

import java.util.UUID;

public record TagResponse(UUID id, String name) {
    public static TagResponse from(Tag t) { return new TagResponse(t.getId(), t.getName()); }
}
