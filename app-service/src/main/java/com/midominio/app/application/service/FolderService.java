package com.midominio.app.application.service;

import com.midominio.app.domain.exception.FolderNotFoundException;
import com.midominio.app.domain.model.Folder;
import com.midominio.app.domain.port.in.CreateFolderCommand;
import com.midominio.app.domain.port.in.FolderUseCase;
import com.midominio.app.domain.port.in.MoveFolderCommand;
import com.midominio.app.domain.port.in.RenameFolderCommand;
import com.midominio.app.domain.port.out.FolderRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public class FolderService implements FolderUseCase {

    private final FolderRepository folderRepository;

    public FolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    @Override
    @Transactional
    public Folder create(CreateFolderCommand cmd) {
        if (cmd.parentId() != null) {
            folderRepository.findByIdAndOwnerId(cmd.parentId(), cmd.ownerId())
                    .orElseThrow(() -> new FolderNotFoundException(cmd.parentId()));
        }
        return folderRepository.save(Folder.create(cmd.name(), cmd.parentId(), cmd.ownerId()));
    }

    @Override
    @Transactional
    public Folder rename(RenameFolderCommand cmd) {
        Folder folder = folderRepository.findByIdAndOwnerId(cmd.folderId(), cmd.ownerId())
                .orElseThrow(() -> new FolderNotFoundException(cmd.folderId()));
        return folderRepository.save(folder.rename(cmd.newName()));
    }

    @Override
    @Transactional
    public Folder move(MoveFolderCommand cmd) {
        Folder folder = folderRepository.findByIdAndOwnerId(cmd.folderId(), cmd.ownerId())
                .orElseThrow(() -> new FolderNotFoundException(cmd.folderId()));
        if (cmd.newParentId() != null) {
            folderRepository.findByIdAndOwnerId(cmd.newParentId(), cmd.ownerId())
                    .orElseThrow(() -> new FolderNotFoundException(cmd.newParentId()));
        }
        return folderRepository.save(folder.move(cmd.newParentId()));
    }

    @Override
    @Transactional
    public void delete(UUID folderId, UUID ownerId) {
        folderRepository.findByIdAndOwnerId(folderId, ownerId)
                .orElseThrow(() -> new FolderNotFoundException(folderId));
        folderRepository.delete(folderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Folder> listContents(UUID parentId, UUID ownerId) {
        return folderRepository.findChildrenByOwnerId(parentId, ownerId);
    }
}
