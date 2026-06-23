package com.midominio.app.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class MediaFile {

    private final UUID id;
    private final String originalName;
    private final String storagePath;
    private final String mimeType;
    private final FileType fileType;
    private final long sizeBytes;
    private final UUID folderId;
    private final UUID ownerId;
    private final FileStatus status;
    private final LocalDateTime createdAt;

    public MediaFile(UUID id, String originalName, String storagePath, String mimeType,
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

    public static MediaFile createProcessing(UUID id, String originalName, String storagePath,
                                             String mimeType, FileType fileType, long sizeBytes,
                                             UUID folderId, UUID ownerId) {
        return new MediaFile(id, originalName, storagePath, mimeType, fileType,
                sizeBytes, folderId, ownerId, FileStatus.PROCESSING, LocalDateTime.now());
    }

    public UUID getId()              { return id; }
    public String getOriginalName()  { return originalName; }
    public String getStoragePath()   { return storagePath; }
    public String getMimeType()      { return mimeType; }
    public FileType getFileType()    { return fileType; }
    public long getSizeBytes()       { return sizeBytes; }
    public UUID getFolderId()        { return folderId; }
    public UUID getOwnerId()         { return ownerId; }
    public FileStatus getStatus()    { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
