package com.midominio.app.infrastructure.out.persistence;

import com.midominio.app.domain.model.Folder;
import com.midominio.app.domain.port.out.FolderRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class FolderRepositoryAdapter implements FolderRepository {

    private final FolderJpaRepository jpa;

    public FolderRepositoryAdapter(FolderJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Folder save(Folder folder) {
        FolderJpaEntity saved = jpa.save(toEntity(folder));
        return toDomain(saved);
    }

    @Override
    public Optional<Folder> findByIdAndOwnerId(UUID id, UUID ownerId) {
        return jpa.findByIdAndOwnerId(id, ownerId).map(this::toDomain);
    }

    @Override
    public List<Folder> findChildrenByOwnerId(UUID parentId, UUID ownerId) {
        if (parentId == null) {
            return jpa.findByParentIdIsNullAndOwnerId(ownerId).stream().map(this::toDomain).toList();
        }
        return jpa.findByParentIdAndOwnerId(parentId, ownerId).stream().map(this::toDomain).toList();
    }

    @Override
    public void delete(UUID id) {
        jpa.deleteById(id);
    }

    private FolderJpaEntity toEntity(Folder f) {
        return new FolderJpaEntity(f.getId(), f.getName(), f.getParentId(), f.getOwnerId(), f.getCreatedAt());
    }

    private Folder toDomain(FolderJpaEntity e) {
        return new Folder(e.getId(), e.getName(), e.getParentId(), e.getOwnerId(), e.getCreatedAt());
    }
}
