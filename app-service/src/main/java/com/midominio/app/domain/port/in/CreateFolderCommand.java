package com.midominio.app.domain.port.in;

import java.util.UUID;

public record CreateFolderCommand(String name, UUID parentId, UUID ownerId) {}
