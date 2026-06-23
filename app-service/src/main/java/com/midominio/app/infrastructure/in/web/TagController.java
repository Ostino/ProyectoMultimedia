package com.midominio.app.infrastructure.in.web;

import com.midominio.app.domain.port.in.CreateTagCommand;
import com.midominio.app.domain.port.in.TagUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class TagController {

    private final TagUseCase tagUseCase;

    public TagController(TagUseCase tagUseCase) {
        this.tagUseCase = tagUseCase;
    }

    /** POST /tags  body: {"name":"vacation"} */
    @PostMapping("/tags")
    public ResponseEntity<TagResponse> create(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        var tag = tagUseCase.createTag(new CreateTagCommand(body.get("name"), ownerId));
        return ResponseEntity.status(HttpStatus.CREATED).body(TagResponse.from(tag));
    }

    /** GET /tags  — all tags belonging to the authenticated user */
    @GetMapping("/tags")
    public List<TagResponse> listOwnerTags(@AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        return tagUseCase.listTagsByOwner(ownerId).stream().map(TagResponse::from).toList();
    }

    /** GET /files/{fileId}/tags */
    @GetMapping("/files/{fileId}/tags")
    public List<TagResponse> listTagsOfFile(
            @PathVariable UUID fileId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        return tagUseCase.listTagsOfFile(fileId, ownerId).stream().map(TagResponse::from).toList();
    }

    /** POST /files/{fileId}/tags  body: {"tagId":"..."} */
    @PostMapping("/files/{fileId}/tags")
    public ResponseEntity<Void> tagFile(
            @PathVariable UUID fileId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        UUID tagId   = UUID.fromString(body.get("tagId"));
        tagUseCase.tagFile(fileId, tagId, ownerId);
        return ResponseEntity.noContent().build();
    }

    /** DELETE /files/{fileId}/tags/{tagId} */
    @DeleteMapping("/files/{fileId}/tags/{tagId}")
    public ResponseEntity<Void> untagFile(
            @PathVariable UUID fileId,
            @PathVariable UUID tagId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        tagUseCase.untagFile(fileId, tagId, ownerId);
        return ResponseEntity.noContent().build();
    }
}
