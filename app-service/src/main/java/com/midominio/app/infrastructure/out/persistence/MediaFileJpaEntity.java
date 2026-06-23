package com.midominio.app.infrastructure.out.persistence;

import com.midominio.app.domain.model.FileStatus;
import com.midominio.app.domain.model.FileType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "media_files")
class MediaFileJpaEntity {

    @Id
    private UUID id;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "folder_id")
    private UUID folderId;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "file_tags",
        joinColumns        = @JoinColumn(name = "file_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<TagJpaEntity> tags = new HashSet<>();

    protected MediaFileJpaEntity() {}

    MediaFileJpaEntity(UUID id, String originalName, String storagePath, String mimeType,
                       FileType fileType, long sizeBytes, UUID folderId, UUID ownerId,
                       FileStatus status, LocalDateTime createdAt) {
        this.id           = id;
        this.originalName = originalName;
        this.storagePath  = storagePath;
        this.mimeType     = mimeType;
        this.fileType     = fileType;
        this.sizeBytes    = sizeBytes;
        this.folderId     = folderId;
        this.ownerId      = ownerId;
        this.status       = status;
        this.createdAt    = createdAt;
    }

    UUID getId()              { return id; }
    String getOriginalName()  { return originalName; }
    String getStoragePath()   { return storagePath; }
    String getMimeType()      { return mimeType; }
    FileType getFileType()    { return fileType; }
    long getSizeBytes()       { return sizeBytes; }
    UUID getFolderId()        { return folderId; }
    UUID getOwnerId()         { return ownerId; }
    FileStatus getStatus()    { return status; }
    LocalDateTime getCreatedAt() { return createdAt; }
    Set<TagJpaEntity> getTags()  { return tags; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediaFileJpaEntity other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }
}
