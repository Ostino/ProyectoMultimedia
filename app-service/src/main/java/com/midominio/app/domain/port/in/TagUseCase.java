package com.midominio.app.domain.port.in;

import com.midominio.app.domain.model.Tag;

import java.util.List;
import java.util.UUID;

public interface TagUseCase {
    Tag         createTag(CreateTagCommand command);
    void        tagFile(UUID fileId, UUID tagId, UUID ownerId);
    void        untagFile(UUID fileId, UUID tagId, UUID ownerId);
    List<Tag>   listTagsOfFile(UUID fileId, UUID ownerId);
    List<Tag>   listTagsByOwner(UUID ownerId);
}
