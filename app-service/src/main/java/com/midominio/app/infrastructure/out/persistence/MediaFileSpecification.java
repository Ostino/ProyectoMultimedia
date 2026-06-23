package com.midominio.app.infrastructure.out.persistence;

import com.midominio.app.domain.model.FileType;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * Package-private factory for JPA Specifications against MediaFileJpaEntity.
 * Metadata filters use correlated EXISTS subqueries to avoid accidental cross joins.
 */
class MediaFileSpecification {

    private MediaFileSpecification() {}

    // ── Generic ─────────────────────────────────────────────────────────────

    static Specification<MediaFileJpaEntity> ownerIs(UUID ownerId) {
        return (r, q, cb) -> cb.equal(r.get("ownerId"), ownerId);
    }

    static Specification<MediaFileJpaEntity> nameContains(String text) {
        String pattern = "%" + text.toLowerCase() + "%";
        return (r, q, cb) -> cb.like(cb.lower(r.get("originalName")), pattern);
    }

    static Specification<MediaFileJpaEntity> createdAfter(LocalDate date) {
        return (r, q, cb) -> cb.greaterThanOrEqualTo(r.get("createdAt"), date.atStartOfDay());
    }

    static Specification<MediaFileJpaEntity> createdBefore(LocalDate date) {
        return (r, q, cb) -> cb.lessThan(r.get("createdAt"), date.plusDays(1).atStartOfDay());
    }

    static Specification<MediaFileJpaEntity> hasFileType(FileType type) {
        return (r, q, cb) -> cb.equal(r.get("fileType"), type);
    }

    static Specification<MediaFileJpaEntity> inFolder(UUID folderId) {
        return (r, q, cb) -> cb.equal(r.get("folderId"), folderId);
    }

    /** File has ANY of the listed tag names (OR semantics). */
    static Specification<MediaFileJpaEntity> hasAnyTag(List<String> names, UUID ownerId) {
        return (r, q, cb) -> {
            Join<MediaFileJpaEntity, TagJpaEntity> j = r.join("tags", JoinType.INNER);
            q.distinct(true);
            return cb.and(j.get("name").in(names), cb.equal(j.get("ownerId"), ownerId));
        };
    }

    // ── Metadata subquery helper ─────────────────────────────────────────────

    /**
     * Builds a correlated EXISTS subquery:
     *   WHERE EXISTS (SELECT 1 FROM metadata m WHERE m.file_id = root.id AND <metaPred>)
     */
    private static Specification<MediaFileJpaEntity> existsMeta(
            BiFunction<Root<MetadataJpaEntity>, CriteriaBuilder, Predicate> metaPred) {
        return (r, q, cb) -> {
            Subquery<Integer> sub = q.subquery(Integer.class);
            Root<MetadataJpaEntity> m = sub.from(MetadataJpaEntity.class);
            sub.select(cb.literal(1))
               .where(cb.and(cb.equal(m.get("fileId"), r.get("id")),
                             metaPred.apply(m, cb)));
            return cb.exists(sub);
        };
    }

    // ── IMAGE ────────────────────────────────────────────────────────────────

    /** Has any camera metadata (typically set by mobile/DSLRs via EXIF). */
    static Specification<MediaFileJpaEntity> hasMobileCamera() {
        return existsMeta((m, cb) -> cb.isNotNull(m.get("cameraMake")));
    }

    static Specification<MediaFileJpaEntity> minWidth(int px) {
        return existsMeta((m, cb) -> cb.greaterThanOrEqualTo(m.get("width"), px));
    }

    static Specification<MediaFileJpaEntity> minHeight(int px) {
        return existsMeta((m, cb) -> cb.greaterThanOrEqualTo(m.get("height"), px));
    }

    static Specification<MediaFileJpaEntity> takenAfter(LocalDate date) {
        return existsMeta((m, cb) ->
            cb.greaterThanOrEqualTo(m.get("takenAt"), date.atStartOfDay()));
    }

    static Specification<MediaFileJpaEntity> takenBefore(LocalDate date) {
        return existsMeta((m, cb) ->
            cb.lessThan(m.get("takenAt"), date.plusDays(1).atStartOfDay()));
    }

    // ── VIDEO ────────────────────────────────────────────────────────────────

    static Specification<MediaFileJpaEntity> minDurationSeconds(double secs) {
        return existsMeta((m, cb) ->
            cb.greaterThanOrEqualTo(m.get("durationSeconds"), secs));
    }

    static Specification<MediaFileJpaEntity> maxDurationSeconds(double secs) {
        return existsMeta((m, cb) ->
            cb.lessThanOrEqualTo(m.get("durationSeconds"), secs));
    }

    static Specification<MediaFileJpaEntity> hasVideoCodec(String codec) {
        return existsMeta((m, cb) ->
            cb.equal(cb.lower(m.get("videoCodec")), codec.toLowerCase()));
    }

    // ── AUDIO ────────────────────────────────────────────────────────────────

    static Specification<MediaFileJpaEntity> artistContains(String text) {
        String p = "%" + text.toLowerCase() + "%";
        return existsMeta((m, cb) -> cb.like(cb.lower(m.get("artist")), p));
    }

    static Specification<MediaFileJpaEntity> albumContains(String text) {
        String p = "%" + text.toLowerCase() + "%";
        return existsMeta((m, cb) -> cb.like(cb.lower(m.get("album")), p));
    }

    // ── DOCUMENT ────────────────────────────────────────────────────────────

    static Specification<MediaFileJpaEntity> minPageCount(int n) {
        return existsMeta((m, cb) -> cb.greaterThanOrEqualTo(m.get("pageCount"), n));
    }

    static Specification<MediaFileJpaEntity> authorContains(String text) {
        String p = "%" + text.toLowerCase() + "%";
        return existsMeta((m, cb) -> cb.like(cb.lower(m.get("author")), p));
    }
}
