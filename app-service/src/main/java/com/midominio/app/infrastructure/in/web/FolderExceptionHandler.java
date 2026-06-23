package com.midominio.app.infrastructure.in.web;

import com.midominio.app.domain.exception.FileNotFoundException;
import com.midominio.app.domain.exception.FolderNotFoundException;
import com.midominio.app.domain.exception.TagAlreadyExistsException;
import com.midominio.app.domain.exception.TagNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
class FolderExceptionHandler {

    @ExceptionHandler(FolderNotFoundException.class)
    ResponseEntity<Map<String, String>> handleFolderNotFound(FolderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(FileNotFoundException.class)
    ResponseEntity<Map<String, String>> handleFileNotFound(FileNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TagNotFoundException.class)
    ResponseEntity<Map<String, String>> handleTagNotFound(TagNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TagAlreadyExistsException.class)
    ResponseEntity<Map<String, String>> handleTagConflict(TagAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }
}
