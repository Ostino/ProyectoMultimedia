package com.midominio.app.domain.model;

import java.util.UUID;

public class Thumbnail {

    private final UUID id;
    private final UUID fileId;
    private final String label;
    private final String storagePath;

    public Thumbnail(UUID id, UUID fileId, String label, String storagePath) {
        this.id          = id;
        this.fileId      = fileId;
        this.label       = label;
        this.storagePath = storagePath;
    }

    public static Thumbnail create(UUID fileId, String label, String storagePath) {
        return new Thumbnail(UUID.randomUUID(), fileId, label, storagePath);
    }

    public UUID getId()           { return id; }
    public UUID getFileId()       { return fileId; }
    public String getLabel()      { return label; }
    public String getStoragePath(){ return storagePath; }
}
