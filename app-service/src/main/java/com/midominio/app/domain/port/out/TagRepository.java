package com.midominio.app.domain.port.out;

import com.midominio.app.domain.model.Tag;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository {
    Tag              save(Tag tag);
    Optional<Tag>    findByIdAndOwnerId(UUID id, UUID ownerId);
    Optional<Tag>    findByNameAndOwnerId(String name, UUID ownerId);
    List<Tag>        findByOwnerId(UUID ownerId);
    List<Tag>        findByFileIdAndOwnerId(UUID fileId, UUID ownerId);
    boolean          existsByNameAndOwnerId(String name, UUID ownerId);
    void             tagFile(UUID fileId, UUID tagId);
    void             untagFile(UUID fileId, UUID tagId);
}
