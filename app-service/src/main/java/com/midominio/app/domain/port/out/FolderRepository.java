package com.midominio.app.domain.port.out;

import com.midominio.app.domain.model.Folder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FolderRepository {
    Folder save(Folder folder);
    Optional<Folder> findByIdAndOwnerId(UUID id, UUID ownerId);
    List<Folder> findChildrenByOwnerId(UUID parentId, UUID ownerId);
    void delete(UUID id);
}
