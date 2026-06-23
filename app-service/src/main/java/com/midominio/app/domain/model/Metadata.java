package com.midominio.app.domain.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class Metadata {

    private final UUID id;
    private final UUID fileId;
    private final Integer width;
    private final Integer height;
    private final Double durationSeconds;
    private final String cameraMake;
    private final String cameraModel;
    private final LocalDateTime takenAt;
    private final String videoCodec;
    private final String audioCodec;
    private final String artist;
    private final String album;
    private final Integer pageCount;
    private final String author;
    private final Map<String, Object> raw;

    public Metadata(UUID id, UUID fileId, Integer width, Integer height, Double durationSeconds,
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

    public static Metadata create(UUID fileId, Integer width, Integer height, Double durationSeconds,
                                  String cameraMake, String cameraModel, LocalDateTime takenAt,
                                  String videoCodec, String audioCodec, String artist, String album,
                                  Integer pageCount, String author, Map<String, Object> raw) {
        return new Metadata(UUID.randomUUID(), fileId, width, height, durationSeconds,
                cameraMake, cameraModel, takenAt, videoCodec, audioCodec, artist, album,
                pageCount, author, raw);
    }

    public UUID getId()                  { return id; }
    public UUID getFileId()              { return fileId; }
    public Integer getWidth()            { return width; }
    public Integer getHeight()           { return height; }
    public Double getDurationSeconds()   { return durationSeconds; }
    public String getCameraMake()        { return cameraMake; }
    public String getCameraModel()       { return cameraModel; }
    public LocalDateTime getTakenAt()    { return takenAt; }
    public String getVideoCodec()        { return videoCodec; }
    public String getAudioCodec()        { return audioCodec; }
    public String getArtist()            { return artist; }
    public String getAlbum()             { return album; }
    public Integer getPageCount()        { return pageCount; }
    public String getAuthor()            { return author; }
    public Map<String, Object> getRaw()  { return raw; }
}
