package com.midominio.app.infrastructure.out.persistence;

import com.midominio.app.domain.model.FileStatus;
import com.midominio.app.domain.model.MediaFile;
import com.midominio.app.domain.model.PageResult;
import com.midominio.app.domain.port.in.SearchCriteria;
import com.midominio.app.domain.port.out.MediaFileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
public class MediaFileRepositoryAdapter implements MediaFileRepository {

    private static final Set<String> ALLOWED_SORT = Set.of(
            "createdAt", "originalName", "sizeBytes", "fileType", "status");

    private final MediaFileJpaRepository jpa;

    public MediaFileRepositoryAdapter(MediaFileJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<MediaFile> findByIdAndOwnerId(UUID id, UUID ownerId) {
        return jpa.findByIdAndOwnerId(id, ownerId).map(this::toDomain);
    }

    @Override
    @Transactional
    public void updateStatus(UUID fileId, FileStatus status) {
        jpa.updateStatus(fileId, status);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public MediaFile save(MediaFile f) {
        MediaFileJpaEntity saved = jpa.save(new MediaFileJpaEntity(
                f.getId(), f.getOriginalName(), f.getStoragePath(), f.getMimeType(),
                f.getFileType(), f.getSizeBytes(), f.getFolderId(), f.getOwnerId(),
                f.getStatus(), f.getCreatedAt()));
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<MediaFile> search(SearchCriteria c, UUID ownerId,
                                        int page, int size, String sortBy, boolean asc) {
        String field = ALLOWED_SORT.contains(sortBy) ? sortBy : "createdAt";
        Sort sort     = asc ? Sort.by(field).ascending() : Sort.by(field).descending();
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), sort);

        Specification<MediaFileJpaEntity> spec = buildSpec(c, ownerId);
        Page<MediaFileJpaEntity> result = jpa.findAll(spec, pageable);
        return new PageResult<>(
                result.getContent().stream().map(this::toDomain).toList(),
                result.getTotalElements(),
                result.getTotalPages(),
                page,
                size);
    }

    private Specification<MediaFileJpaEntity> buildSpec(SearchCriteria c, UUID ownerId) {
        Specification<MediaFileJpaEntity> s = MediaFileSpecification.ownerIs(ownerId);

        if (hasText(c.name()))
            s = s.and(MediaFileSpecification.nameContains(c.name()));
        if (c.fromDate() != null)
            s = s.and(MediaFileSpecification.createdAfter(c.fromDate()));
        if (c.toDate() != null)
            s = s.and(MediaFileSpecification.createdBefore(c.toDate()));
        if (c.fileType() != null)
            s = s.and(MediaFileSpecification.hasFileType(c.fileType()));
        if (c.folderId() != null)
            s = s.and(MediaFileSpecification.inFolder(c.folderId()));
        if (c.tagNames() != null && !c.tagNames().isEmpty())
            s = s.and(MediaFileSpecification.hasAnyTag(c.tagNames(), ownerId));

        // Image
        if (Boolean.TRUE.equals(c.mobileCamera()))
            s = s.and(MediaFileSpecification.hasMobileCamera());
        if (c.minWidth() != null)
            s = s.and(MediaFileSpecification.minWidth(c.minWidth()));
        if (c.minHeight() != null)
            s = s.and(MediaFileSpecification.minHeight(c.minHeight()));
        if (c.takenFrom() != null)
            s = s.and(MediaFileSpecification.takenAfter(c.takenFrom()));
        if (c.takenTo() != null)
            s = s.and(MediaFileSpecification.takenBefore(c.takenTo()));

        // Video
        if (c.minDuration() != null)
            s = s.and(MediaFileSpecification.minDurationSeconds(c.minDuration()));
        if (c.maxDuration() != null)
            s = s.and(MediaFileSpecification.maxDurationSeconds(c.maxDuration()));
        if (hasText(c.videoCodec()))
            s = s.and(MediaFileSpecification.hasVideoCodec(c.videoCodec()));
        if (c.minVideoWidth() != null)
            s = s.and(MediaFileSpecification.minWidth(c.minVideoWidth()));

        // Audio
        if (hasText(c.artist()))
            s = s.and(MediaFileSpecification.artistContains(c.artist()));
        if (hasText(c.album()))
            s = s.and(MediaFileSpecification.albumContains(c.album()));

        // Document
        if (c.minPageCount() != null)
            s = s.and(MediaFileSpecification.minPageCount(c.minPageCount()));
        if (hasText(c.pdfAuthor()))
            s = s.and(MediaFileSpecification.authorContains(c.pdfAuthor()));

        return s;
    }

    private boolean hasText(String s) { return s != null && !s.isBlank(); }

    private MediaFile toDomain(MediaFileJpaEntity e) {
        return new MediaFile(e.getId(), e.getOriginalName(), e.getStoragePath(), e.getMimeType(),
                e.getFileType(), e.getSizeBytes(), e.getFolderId(), e.getOwnerId(),
                e.getStatus(), e.getCreatedAt());
    }
}
