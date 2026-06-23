package com.midominio.app.infrastructure.out.persistence;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "thumbnails")
class ThumbnailJpaEntity {

    @Id
    private UUID id;

    @Column(name = "file_id", nullable = false)
    private UUID fileId;

    @Column(nullable = false)
    private String label;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    protected ThumbnailJpaEntity() {}

    ThumbnailJpaEntity(UUID id, UUID fileId, String label, String storagePath) {
        this.id          = id;
        this.fileId      = fileId;
        this.label       = label;
        this.storagePath = storagePath;
    }

    UUID getId()            { return id; }
    UUID getFileId()        { return fileId; }
    String getLabel()       { return label; }
    String getStoragePath() { return storagePath; }
}
