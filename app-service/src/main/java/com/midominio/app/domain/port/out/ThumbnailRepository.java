package com.midominio.app.domain.port.out;

import com.midominio.app.domain.model.Thumbnail;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ThumbnailRepository {
    void saveAll(List<Thumbnail> thumbnails);
    List<Thumbnail> findByFileId(UUID fileId);
    Optional<Thumbnail> findByFileIdAndLabel(UUID fileId, String label);
}
