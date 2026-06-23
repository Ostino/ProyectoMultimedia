package com.midominio.app.domain.port.in;

import com.midominio.app.domain.model.Folder;

import java.util.List;
import java.util.UUID;

public interface FolderUseCase {
    Folder create(CreateFolderCommand command);
    Folder rename(RenameFolderCommand command);
    Folder move(MoveFolderCommand command);
    void delete(UUID folderId, UUID ownerId);
    List<Folder> listContents(UUID parentId, UUID ownerId);
}
