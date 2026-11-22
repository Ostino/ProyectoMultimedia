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

    /**
     * Asigna al archivo las etiquetas sugeridas automáticamente (IA), creando las
     * que el dueño todavía no tenga. Los nombres se normalizan (minúsculas, sin
     * espacios sobrantes) y se descartan los vacíos o repetidos.
     */
    void        applyAutoTags(UUID fileId, UUID ownerId, List<String> tagNames);
}
