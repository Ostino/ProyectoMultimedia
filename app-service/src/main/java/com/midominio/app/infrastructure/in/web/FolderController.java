package com.midominio.app.infrastructure.in.web;

import com.midominio.app.domain.port.in.CreateFolderCommand;
import com.midominio.app.domain.port.in.FolderUseCase;
import com.midominio.app.domain.port.in.MoveFolderCommand;
import com.midominio.app.domain.port.in.RenameFolderCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/folders")
public class FolderController {

    private final FolderUseCase folderUseCase;

    public FolderController(FolderUseCase folderUseCase) {
        this.folderUseCase = folderUseCase;
    }

    @PostMapping
    public ResponseEntity<FolderResponse> create(
            @RequestBody CreateFolderRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        var folder = folderUseCase.create(new CreateFolderCommand(request.name(), request.parentId(), ownerId));
        return ResponseEntity.status(HttpStatus.CREATED).body(FolderResponse.from(folder));
    }

    @GetMapping
    public List<FolderResponse> listContents(
            @RequestParam(required = false) UUID parentId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        return folderUseCase.listContents(parentId, ownerId).stream().map(FolderResponse::from).toList();
    }

    @PatchMapping("/{id}/name")
    public FolderResponse rename(
            @PathVariable UUID id,
            @RequestBody RenameFolderRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        return FolderResponse.from(folderUseCase.rename(new RenameFolderCommand(id, request.name(), ownerId)));
    }

    @PatchMapping("/{id}/parent")
    public FolderResponse move(
            @PathVariable UUID id,
            @RequestBody MoveFolderRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        return FolderResponse.from(folderUseCase.move(new MoveFolderCommand(id, request.parentId(), ownerId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        folderUseCase.delete(id, ownerId);
        return ResponseEntity.noContent().build();
    }
}
