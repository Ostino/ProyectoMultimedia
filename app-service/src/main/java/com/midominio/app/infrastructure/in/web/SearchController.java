package com.midominio.app.infrastructure.in.web;

import com.midominio.app.domain.model.FileType;
import com.midominio.app.domain.model.MediaFile;
import com.midominio.app.domain.model.PageResult;
import com.midominio.app.domain.port.in.SearchCriteria;
import com.midominio.app.domain.port.in.SearchUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/files/search")
public class SearchController {

    private final SearchUseCase searchUseCase;

    public SearchController(SearchUseCase searchUseCase) {
        this.searchUseCase = searchUseCase;
    }

    /**
     * Universal search endpoint with dynamic filters.
     *
     * Generic:   name, fromDate, toDate, fileType, folderId, tags
     * Image:     mobileCamera=true, minWidth, minHeight, takenFrom, takenTo
     * Video:     minDuration, maxDuration, videoCodec, minVideoWidth
     * Audio:     artist, album, minDuration, maxDuration
     * Document:  minPageCount, pdfAuthor
     */
    @GetMapping
    public ResponseEntity<PageResponse<SearchResultResponse>> search(
            // ── Generic ─────────────────────────────────────────────────────
            @RequestParam(required = false) String name,
            @RequestParam(required = false) FileType fileType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) UUID folderId,
            @RequestParam(required = false) List<String> tags,
            // ── Image ────────────────────────────────────────────────────────
            @RequestParam(required = false) Boolean mobileCamera,
            @RequestParam(required = false) Integer minWidth,
            @RequestParam(required = false) Integer minHeight,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate takenFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate takenTo,
            // ── Video ────────────────────────────────────────────────────────
            @RequestParam(required = false) Double minDuration,
            @RequestParam(required = false) Double maxDuration,
            @RequestParam(required = false) String videoCodec,
            @RequestParam(required = false) Integer minVideoWidth,
            // ── Audio ────────────────────────────────────────────────────────
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String album,
            // ── Document ────────────────────────────────────────────────────
            @RequestParam(required = false) Integer minPageCount,
            @RequestParam(required = false) String pdfAuthor,
            // ── Pagination ───────────────────────────────────────────────────
            @RequestParam(defaultValue = "0")         int    page,
            @RequestParam(defaultValue = "20")        int    size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc")      String sortDir,
            @AuthenticationPrincipal Jwt jwt) {

        UUID ownerId = UUID.fromString(jwt.getSubject());
        boolean asc  = "asc".equalsIgnoreCase(sortDir);

        SearchCriteria criteria = new SearchCriteria(
                name, fromDate, toDate, fileType, folderId, tags,
                mobileCamera, minWidth, minHeight, takenFrom, takenTo,
                minDuration, maxDuration, videoCodec, minVideoWidth,
                artist, album,
                minPageCount, pdfAuthor);

        PageResult<MediaFile> result = searchUseCase.search(criteria, ownerId, page, size, sortBy, asc);

        List<SearchResultResponse> content = result.content().stream()
                .map(SearchResultResponse::from)
                .toList();

        return ResponseEntity.ok(new PageResponse<>(
                content, result.page(), result.size(),
                result.totalElements(), result.totalPages()));
    }
}
