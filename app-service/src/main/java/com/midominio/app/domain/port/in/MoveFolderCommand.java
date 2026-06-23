package com.midominio.app.domain.port.in;

import java.util.UUID;

public record MoveFolderCommand(UUID folderId, UUID newParentId, UUID ownerId) {}
