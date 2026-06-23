package com.midominio.app.infrastructure.in.web;

import java.util.UUID;

public record CreateFolderRequest(String name, UUID parentId) {}
