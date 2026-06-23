package com.midominio.app.infrastructure.in.web;

import com.midominio.app.domain.exception.FileNotFoundException;
import com.midominio.app.domain.model.FileDetail;
import com.midominio.app.domain.port.in.DeleteFileUseCase;
import com.midominio.app.domain.port.in.FileQueryUseCase;
import com.midominio.app.domain.port.out.ThumbnailRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class MediaFileController {

    private final FileQueryUseCase    fileQueryUseCase;
    private final ThumbnailRepository thumbnailRepository;
    private final DeleteFileUseCase   deleteFileUseCase;

    public MediaFileController(FileQueryUseCase fileQueryUseCase,
                               ThumbnailRepository thumbnailRepository,
                               DeleteFileUseCase deleteFileUseCase) {
        this.fileQueryUseCase    = fileQueryUseCase;
        this.thumbnailRepository = thumbnailRepository;
        this.deleteFileUseCase   = deleteFileUseCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileDetailResponse> getDetail(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        FileDetail detail = fileQueryUseCase.getDetail(id, ownerId);
        return ResponseEntity.ok(FileDetailResponse.from(detail));
    }

    @GetMapping("/{id}/original")
    public ResponseEntity<Resource> serveOriginal(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        FileDetail detail = fileQueryUseCase.getDetail(id, ownerId);
        Resource resource = new FileSystemResource(detail.file().getStoragePath());
        return ResponseEntity.ok()
                .contentType(parseContentType(detail.file().getMimeType()))
                .body(resource);
    }

    @GetMapping("/{id}/derivative/{label}")
    public ResponseEntity<Resource> serveDerivative(
            @PathVariable UUID id,
            @PathVariable String label,
            @AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        // ownership check via detail query
        fileQueryUseCase.getDetail(id, ownerId);

        var thumbnail = thumbnailRepository.findByFileIdAndLabel(id, label)
                .orElseThrow(() -> new FileNotFoundException(id));

        Resource resource = new FileSystemResource(Path.of(thumbnail.getStoragePath()));
        return ResponseEntity.ok()
                .contentType(guessContentType(thumbnail.getStoragePath()))
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        deleteFileUseCase.delete(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    private MediaType parseContentType(String mimeType) {
        try { return MediaType.parseMediaType(mimeType); }
        catch (Exception e) { return MediaType.APPLICATION_OCTET_STREAM; }
    }

    private MediaType guessContentType(String path) {
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (path.endsWith(".png"))  return MediaType.IMAGE_PNG;
        if (path.endsWith(".webp")) return MediaType.parseMediaType("image/webp");
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
