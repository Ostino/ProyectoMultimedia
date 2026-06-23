package com.midominio.app.domain.port.in;

import com.midominio.app.domain.model.FileType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * All fields are nullable — null means "no filter applied" for that dimension.
 */
public record SearchCriteria(
        // ── Generic ─────────────────────────────────────────────────────────
        String       name,          // originalName contains (case-insensitive)
        LocalDate    fromDate,       // createdAt ≥ fromDate
        LocalDate    toDate,         // createdAt ≤ toDate
        FileType     fileType,
        UUID         folderId,
        List<String> tagNames,       // file has ANY of these tags (OR)

        // ── Image ────────────────────────────────────────────────────────────
        Boolean      mobileCamera,   // camera_make IS NOT NULL
        Integer      minWidth,
        Integer      minHeight,
        LocalDate    takenFrom,      // metadata.taken_at ≥ takenFrom
        LocalDate    takenTo,        // metadata.taken_at ≤ takenTo

        // ── Video ────────────────────────────────────────────────────────────
        Double       minDuration,    // duration_seconds ≥ (shared with audio)
        Double       maxDuration,
        String       videoCodec,     // exact match (case-insensitive)
        Integer      minVideoWidth,  // video resolution: metadata.width ≥

        // ── Audio ────────────────────────────────────────────────────────────
        String       artist,         // contains
        String       album,          // contains

        // ── Document (PDF) ───────────────────────────────────────────────────
        Integer      minPageCount,
        String       pdfAuthor       // metadata.author contains
) {}
