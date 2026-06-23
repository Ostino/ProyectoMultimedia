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

import java.util.List;
import java.util.UUID;

public class TagService implements TagUseCase {

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
}
