package com.midominio.app.application.service;

import com.midominio.app.domain.model.FileStatus;
import com.midominio.app.domain.model.FileType;
import com.midominio.app.domain.model.MediaFile;
import com.midominio.app.domain.port.out.MediaFileRepository;
import com.midominio.app.domain.port.out.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PurgeExpiredFilesServiceTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 22, 18, 0);

    private MediaFileRepository repository;
    private StorageService storage;
    private PurgeExpiredFilesService service;

    @BeforeEach
    void setUp() {
        repository = mock(MediaFileRepository.class);
        storage = mock(StorageService.class);
        service = new PurgeExpiredFilesService(repository, storage);
    }

    @Test
    void borraLosArchivosExpiradosDeLaBaseYDelDisco() {
        MediaFile expired = file(NOW.minusHours(2));
        LocalDateTime cutoff = NOW.minusHours(1);
        when(repository.findCreatedBefore(cutoff)).thenReturn(List.of(expired));

        int deleted = service.purge(cutoff, null);

        assertThat(deleted).isEqualTo(1);
        verify(repository).delete(expired.getId());
        verify(storage).deleteFileDirectory(expired.getStoragePath());
    }

    @Test
    void conservaLosArchivosCreadosAntesDeLaFechaProtegida() {
        MediaFile sample = file(NOW.minusDays(3));
        MediaFile upload = file(NOW.minusHours(2));
        LocalDateTime cutoff = NOW.minusHours(1);
        when(repository.findCreatedBefore(cutoff)).thenReturn(List.of(sample, upload));

        int deleted = service.purge(cutoff, NOW.minusDays(1));

        assertThat(deleted).isEqualTo(1);
        verify(repository, never()).delete(sample.getId());
        verify(repository).delete(upload.getId());
    }

    @Test
    void siUnArchivoFallaSigueConLosDemas() {
        MediaFile broken = file(NOW.minusHours(3));
        MediaFile ok = file(NOW.minusHours(2));
        LocalDateTime cutoff = NOW.minusHours(1);
        when(repository.findCreatedBefore(cutoff)).thenReturn(List.of(broken, ok));
        doThrow(new RuntimeException("db caída")).when(repository).delete(broken.getId());

        int deleted = service.purge(cutoff, null);

        assertThat(deleted).isEqualTo(1);
        verify(storage, never()).deleteFileDirectory(broken.getStoragePath());
        verify(storage).deleteFileDirectory(ok.getStoragePath());
    }

    private static MediaFile file(LocalDateTime createdAt) {
        UUID id = UUID.randomUUID();
        return new MediaFile(id, "foto.jpg", "/data/" + id + "/original.jpg", "image/jpeg",
                FileType.IMAGE, 1024, null, UUID.randomUUID(), FileStatus.READY, createdAt);
    }
}
