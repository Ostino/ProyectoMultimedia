package com.midominio.app.infrastructure.out.persistence;

import com.midominio.app.domain.model.Metadata;
import com.midominio.app.domain.port.out.MetadataRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class MetadataRepositoryAdapter implements MetadataRepository {

    private final MetadataJpaRepository jpa;

    public MetadataRepositoryAdapter(MetadataJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Metadata m) {
        jpa.save(new MetadataJpaEntity(
                m.getId(), m.getFileId(), m.getWidth(), m.getHeight(),
                m.getDurationSeconds(), m.getCameraMake(), m.getCameraModel(),
                m.getTakenAt(), m.getVideoCodec(), m.getAudioCodec(),
                m.getArtist(), m.getAlbum(), m.getPageCount(), m.getAuthor(), m.getRaw()));
    }

    @Override
    public Optional<Metadata> findByFileId(UUID fileId) {
        return jpa.findByFileId(fileId).map(this::toDomain);
    }

    private Metadata toDomain(MetadataJpaEntity e) {
        return new Metadata(e.getId(), e.getFileId(), e.getWidth(), e.getHeight(),
                e.getDurationSeconds(), e.getCameraMake(), e.getCameraModel(),
                e.getTakenAt(), e.getVideoCodec(), e.getAudioCodec(),
                e.getArtist(), e.getAlbum(), e.getPageCount(), e.getAuthor(), e.getRaw());
    }
}
