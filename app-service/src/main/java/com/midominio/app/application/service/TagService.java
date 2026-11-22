package com.midominio.app.application.service;

import com.midominio.app.domain.exception.FileNotFoundException;
import com.midominio.app.domain.exception.TagAlreadyExistsException;
import com.midominio.app.domain.exception.TagNotFoundException;
import com.midominio.app.domain.model.Tag;
import com.midominio.app.domain.port.in.CreateTagCommand;
import com.midominio.app.domain.port.in.TagUseCase;
import com.midominio.app.domain.port.out.MediaFileRepository;
import com.midominio.app.domain.port.out.TagRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class TagService implements TagUseCase {

    private static final int MAX_TAG_LENGTH = 100;   // tags.name VARCHAR(100)

    private final TagRepository       tagRepository;
    private final MediaFileRepository mediaFileRepository;

    public TagService(TagRepository tagRepository, MediaFileRepository mediaFileRepository) {
        this.tagRepository       = tagRepository;
        this.mediaFileRepository = mediaFileRepository;
    }

    @Override
    @Transactional
    public Tag createTag(CreateTagCommand command) {
        if (tagRepository.existsByNameAndOwnerId(command.name(), command.ownerId())) {
            throw new TagAlreadyExistsException(command.name());
        }
        return tagRepository.save(Tag.create(command.name(), command.ownerId()));
    }

    @Override
    @Transactional
    public void tagFile(UUID fileId, UUID tagId, UUID ownerId) {
        mediaFileRepository.findByIdAndOwnerId(fileId, ownerId)
                .orElseThrow(() -> new FileNotFoundException(fileId));
        tagRepository.findByIdAndOwnerId(tagId, ownerId)
                .orElseThrow(() -> new TagNotFoundException(tagId));
        tagRepository.tagFile(fileId, tagId);
    }

    @Override
    @Transactional
    public void untagFile(UUID fileId, UUID tagId, UUID ownerId) {
        tagRepository.findByIdAndOwnerId(tagId, ownerId)
                .orElseThrow(() -> new TagNotFoundException(tagId));
        tagRepository.untagFile(fileId, tagId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> listTagsOfFile(UUID fileId, UUID ownerId) {
        mediaFileRepository.findByIdAndOwnerId(fileId, ownerId)
                .orElseThrow(() -> new FileNotFoundException(fileId));
        return tagRepository.findByFileIdAndOwnerId(fileId, ownerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> listTagsByOwner(UUID ownerId) {
        return tagRepository.findByOwnerId(ownerId);
    }

    @Override
    @Transactional
    public void applyAutoTags(UUID fileId, UUID ownerId, List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) return;
        // El archivo pudo borrarse mientras se procesaba
        if (mediaFileRepository.findByIdAndOwnerId(fileId, ownerId).isEmpty()) return;

        for (String name : normalize(tagNames)) {
            Tag tag = tagRepository.findByNameAndOwnerId(name, ownerId)
                    .orElseGet(() -> tagRepository.save(Tag.create(name, ownerId)));
            tagRepository.tagFile(fileId, tag.getId());
        }
    }

    private static Set<String> normalize(List<String> names) {
        Set<String> result = new LinkedHashSet<>();
        for (String n : names) {
            if (n == null) continue;
            String clean = n.trim().replaceFirst("^#+", "").replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
            if (clean.isEmpty()) continue;
            result.add(clean.length() > MAX_TAG_LENGTH ? clean.substring(0, MAX_TAG_LENGTH) : clean);
        }
        return result;
    }
}
