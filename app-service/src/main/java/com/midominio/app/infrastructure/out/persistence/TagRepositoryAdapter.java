package com.midominio.app.infrastructure.out.persistence;

import com.midominio.app.domain.model.Tag;
import com.midominio.app.domain.port.out.TagRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TagRepositoryAdapter implements TagRepository {

    private final TagJpaRepository       tagJpa;
    private final MediaFileJpaRepository fileJpa;

    public TagRepositoryAdapter(TagJpaRepository tagJpa, MediaFileJpaRepository fileJpa) {
        this.tagJpa  = tagJpa;
        this.fileJpa = fileJpa;
    }

    @Override
    public Tag save(Tag tag) {
        TagJpaEntity saved = tagJpa.save(new TagJpaEntity(tag.getId(), tag.getName(), tag.getOwnerId()));
        return toDomain(saved);
    }

    @Override
    public Optional<Tag> findByIdAndOwnerId(UUID id, UUID ownerId) {
        return tagJpa.findByIdAndOwnerId(id, ownerId).map(this::toDomain);
    }

    @Override
    public Optional<Tag> findByNameAndOwnerId(String name, UUID ownerId) {
        return tagJpa.findByNameAndOwnerId(name, ownerId).map(this::toDomain);
    }

    @Override
    public List<Tag> findByOwnerId(UUID ownerId) {
        return tagJpa.findByOwnerId(ownerId).stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> findByFileIdAndOwnerId(UUID fileId, UUID ownerId) {
        return fileJpa.findByIdAndOwnerId(fileId, ownerId)
                .map(f -> f.getTags().stream()
                        .filter(t -> t.getOwnerId().equals(ownerId))
                        .map(this::toDomain)
                        .toList())
                .orElse(List.of());
    }

    @Override
    public boolean existsByNameAndOwnerId(String name, UUID ownerId) {
        return tagJpa.existsByNameAndOwnerId(name, ownerId);
    }

    @Override
    @Transactional
    public void tagFile(UUID fileId, UUID tagId) {
        fileJpa.findById(fileId).ifPresent(file -> {
            tagJpa.findById(tagId).ifPresent(tag -> file.getTags().add(tag));
        });
    }

    @Override
    @Transactional
    public void untagFile(UUID fileId, UUID tagId) {
        fileJpa.findById(fileId).ifPresent(file -> {
            tagJpa.findById(tagId).ifPresent(tag -> file.getTags().remove(tag));
        });
    }

    private Tag toDomain(TagJpaEntity e) {
        return new Tag(e.getId(), e.getName(), e.getOwnerId());
    }
}
