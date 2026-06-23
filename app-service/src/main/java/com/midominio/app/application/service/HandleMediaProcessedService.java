package com.midominio.app.application.service;

import com.midominio.app.domain.model.FileStatus;
import com.midominio.app.domain.model.Metadata;
import com.midominio.app.domain.model.Thumbnail;
import com.midominio.app.domain.port.in.HandleMediaProcessedCommand;
import com.midominio.app.domain.port.in.HandleMediaProcessedUseCase;
import com.midominio.app.domain.port.out.MediaFileRepository;
import com.midominio.app.domain.port.out.MetadataRepository;
import com.midominio.app.domain.port.out.ThumbnailRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HandleMediaProcessedService implements HandleMediaProcessedUseCase {

    private static final DateTimeFormatter EXIF_FMT = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

    private final MediaFileRepository mediaFileRepository;
    private final MetadataRepository  metadataRepository;
    private final ThumbnailRepository thumbnailRepository;

    public HandleMediaProcessedService(MediaFileRepository mediaFileRepository,
                                       MetadataRepository metadataRepository,
                                       ThumbnailRepository thumbnailRepository) {
        this.mediaFileRepository = mediaFileRepository;
        this.metadataRepository  = metadataRepository;
        this.thumbnailRepository = thumbnailRepository;
    }

    @Override
    @Transactional
    public void handle(HandleMediaProcessedCommand cmd) {
        Map<String, Object> raw = cmd.rawMetadata();

        Metadata metadata = Metadata.create(
                cmd.fileId(),
                parseInt(raw.get("width")),
                parseInt(raw.get("height")),
                parseDouble(raw.get("duration")),
                str(raw.get("make")),
                str(raw.get("model")),
                parseTakenAt(raw.get("dateCaptured")),
                str(raw.get("videoCodec")),
                str(raw.get("audioCodec")),
                str(raw.get("artist")),
                str(raw.get("album")),
                parseInt(raw.get("pageCount")),
                str(raw.get("author")),
                raw);

        metadataRepository.save(metadata);

        List<Thumbnail> thumbnails = cmd.derivatives().entrySet().stream()
                .map(e -> Thumbnail.create(cmd.fileId(), e.getKey(), e.getValue()))
                .toList();
        if (!thumbnails.isEmpty()) {
            thumbnailRepository.saveAll(thumbnails);
        }

        mediaFileRepository.updateStatus(cmd.fileId(), FileStatus.READY);
    }

    private Integer parseInt(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.intValue();
        try { return Integer.parseInt(v.toString()); } catch (Exception e) { return null; }
    }

    private Double parseDouble(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(v.toString()); } catch (Exception e) { return null; }
    }

    private String str(Object v) {
        return v == null ? null : v.toString();
    }

    private LocalDateTime parseTakenAt(Object v) {
        if (v == null) return null;
        try { return LocalDateTime.parse(v.toString(), EXIF_FMT); } catch (Exception e) { return null; }
    }
}
