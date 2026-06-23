package com.midominio.app.infrastructure.out.persistence;

import com.midominio.app.domain.model.Thumbnail;
import com.midominio.app.domain.port.out.ThumbnailRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ThumbnailRepositoryAdapter implements ThumbnailRepository {

    private final ThumbnailJpaRepository jpa;

    public ThumbnailRepositoryAdapter(ThumbnailJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void saveAll(List<Thumbnail> thumbnails) {
        jpa.saveAll(thumbnails.stream()
                .map(t -> new ThumbnailJpaEntity(t.getId(), t.getFileId(), t.getLabel(), t.getStoragePath()))
                .toList());
    }

    @Override
    public List<Thumbnail> findByFileId(UUID fileId) {
        return jpa.findByFileId(fileId).stream()
                .map(e -> new Thumbnail(e.getId(), e.getFileId(), e.getLabel(), e.getStoragePath()))
                .toList();
    }

    @Override
    public Optional<Thumbnail> findByFileIdAndLabel(UUID fileId, String label) {
        return jpa.findByFileIdAndLabel(fileId, label)
                .map(e -> new Thumbnail(e.getId(), e.getFileId(), e.getLabel(), e.getStoragePath()));
    }
}
