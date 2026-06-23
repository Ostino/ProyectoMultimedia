package com.midominio.optimizer.domain.model;

import java.util.UUID;

public class MediaFile {

    private final UUID id;
    private final UUID ownerId;
    private final String storagePath;
    private final FileType fileType;

    public MediaFile(UUID id, UUID ownerId, String storagePath, FileType fileType) {
        this.id          = id;
        this.ownerId     = ownerId;
        this.storagePath = storagePath;
        this.fileType    = fileType;
    }

    public UUID getId()           { return id; }
    public UUID getOwnerId()      { return ownerId; }
    public String getStoragePath(){ return storagePath; }
    public FileType getFileType() { return fileType; }
}
