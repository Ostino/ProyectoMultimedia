package com.midominio.app.infrastructure.in.web;

import com.midominio.app.domain.model.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record FileDetailResponse(
        UUID id,
        String originalName,
        String mimeType,
        FileType fileType,
        long sizeBytes,
        UUID folderId,
        FileStatus status,
        LocalDateTime createdAt,
        MetadataDto metadata,
        Map<String, String> derivativeUrls) {

    public static FileDetailResponse from(FileDetail detail) {
        var file = detail.file();
        UUID id  = file.getId();

        Map<String, String> urls = new HashMap<>();
        urls.put("original", "/files/" + id + "/original");
        detail.derivatives().forEach(t ->
                urls.put(t.getLabel(), "/files/" + id + "/derivative/" + t.getLabel()));

        MetadataDto metaDto = detail.metadata().map(MetadataDto::from).orElse(null);

        return new FileDetailResponse(id, file.getOriginalName(), file.getMimeType(),
                file.getFileType(), file.getSizeBytes(), file.getFolderId(),
                file.getStatus(), file.getCreatedAt(), metaDto, urls);
    }

    public record MetadataDto(
            Integer width, Integer height, Double durationSeconds,
            String cameraMake, String cameraModel, LocalDateTime takenAt,
            String videoCodec, String audioCodec,
            String artist, String album, Integer pageCount, String author,
            Map<String, Object> raw) {

        static MetadataDto from(Metadata m) {
            return new MetadataDto(m.getWidth(), m.getHeight(), m.getDurationSeconds(),
                    m.getCameraMake(), m.getCameraModel(), m.getTakenAt(),
                    m.getVideoCodec(), m.getAudioCodec(),
                    m.getArtist(), m.getAlbum(), m.getPageCount(), m.getAuthor(), m.getRaw());
        }
    }
}
