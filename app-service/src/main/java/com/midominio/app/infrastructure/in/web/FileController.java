package com.midominio.app.infrastructure.in.web;

import com.midominio.app.domain.port.in.UploadFileCommand;
import com.midominio.app.domain.port.in.UploadFileUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {

    private final UploadFileUseCase uploadFileUseCase;

    public FileController(UploadFileUseCase uploadFileUseCase) {
        this.uploadFileUseCase = uploadFileUseCase;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) UUID folderId,
            @AuthenticationPrincipal Jwt jwt) throws IOException {

        UUID ownerId = UUID.fromString(jwt.getSubject());

        var command = new UploadFileCommand(
                file.getOriginalFilename(),
                file.getBytes(),
                folderId,
                ownerId);

        var result = uploadFileUseCase.upload(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(FileResponse.from(result));
    }
}
