package com.midominio.app.domain.port.in;

import java.util.UUID;

public record RenameFolderCommand(UUID folderId, String newName, UUID ownerId) {}
