package com.midominio.app.infrastructure.out.persistence;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "metadata")
class MetadataJpaEntity {

    @Id
    private UUID id;

    @Column(name = "file_id", nullable = false, unique = true)
    private UUID fileId;

    private Integer width;
    private Integer height;

    @Column(name = "duration_seconds", columnDefinition = "numeric(12,3)")
    private Double durationSeconds;

    @Column(name = "camera_make")
    private String cameraMake;

    @Column(name = "camera_model")
    private String cameraModel;

    @Column(name = "taken_at")
    private LocalDateTime takenAt;

    @Column(name = "video_codec")
    private String videoCodec;

    @Column(name = "audio_codec")
    private String audioCodec;

    private String artist;
    private String album;

    @Column(name = "page_count")
    private Integer pageCount;

    private String author;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> raw;

    protected MetadataJpaEntity() {}

    MetadataJpaEntity(UUID id, UUID fileId, Integer width, Integer height, Double durationSeconds,
                      String cameraMake, String cameraModel, LocalDateTime takenAt,
                      String videoCodec, String audioCodec, String artist, String album,
                      Integer pageCount, String author, Map<String, Object> raw) {
        this.id              = id;
        this.fileId          = fileId;
        this.width           = width;
        this.height          = height;
        this.durationSeconds = durationSeconds;
        this.cameraMake      = cameraMake;
        this.cameraModel     = cameraModel;
        this.takenAt         = takenAt;
        this.videoCodec      = videoCodec;
        this.audioCodec      = audioCodec;
        this.artist          = artist;
        this.album           = album;
        this.pageCount       = pageCount;
        this.author          = author;
        this.raw             = raw;
    }

    UUID getId()                  { return id; }
    UUID getFileId()              { return fileId; }
    Integer getWidth()            { return width; }
    Integer getHeight()           { return height; }
    Double getDurationSeconds()   { return durationSeconds; }
    String getCameraMake()        { return cameraMake; }
    String getCameraModel()       { return cameraModel; }
    LocalDateTime getTakenAt()    { return takenAt; }
    String getVideoCodec()        { return videoCodec; }
    String getAudioCodec()        { return audioCodec; }
    String getArtist()            { return artist; }
    String getAlbum()             { return album; }
    Integer getPageCount()        { return pageCount; }
    String getAuthor()            { return author; }
    Map<String, Object> getRaw()  { return raw; }
}
