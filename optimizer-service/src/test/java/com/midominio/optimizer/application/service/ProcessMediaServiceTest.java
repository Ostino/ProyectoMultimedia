package com.midominio.optimizer.application.service;

import com.midominio.optimizer.domain.model.FileType;
import com.midominio.optimizer.domain.model.MediaFile;
import com.midominio.optimizer.domain.model.ProcessingResult;
import com.midominio.optimizer.domain.port.out.AutoTagger;
import com.midominio.optimizer.domain.port.out.MediaProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProcessMediaServiceTest {

    private final MediaFile file =
            new MediaFile(UUID.randomUUID(), UUID.randomUUID(), "/data/x/original.jpg", FileType.IMAGE);

    private MediaProcessor processor;
    private AutoTagger tagger;
    private ProcessMediaService service;

    @BeforeEach
    void setUp() {
        processor = mock(MediaProcessor.class);
        tagger = mock(AutoTagger.class);
        when(processor.supports(FileType.IMAGE)).thenReturn(true);
        service = new ProcessMediaService(List.of(processor), tagger);
    }

    @Test
    void siGeminiFallaElArchivoSeProcesaIgualSinEtiquetasDeIa() {
        ProcessingResult processed = ProcessingResult.success(file.getId(), file.getOwnerId(),
                Map.of("thumb_512", "/data/x/thumbnails/thumb_512.jpg"), Map.of("width", 800));
        when(processor.process(file)).thenReturn(processed);
        when(tagger.suggestTags(any(), any())).thenThrow(new RuntimeException("429 Too Many Requests"));

        ProcessingResult result = service.process(file);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getDerivatives()).containsKey("thumb_512");
        assertThat(result.getMetadata()).containsEntry("width", 800);
        assertThat(result.getAiTags()).isEmpty();
    }

    @Test
    void agregaLasEtiquetasSugeridasCuandoGeminiResponde() {
        when(processor.process(file)).thenReturn(
                ProcessingResult.success(file.getId(), file.getOwnerId(), Map.of(), Map.of()));
        when(tagger.suggestTags(any(), any())).thenReturn(List.of("playa", "atardecer"));

        ProcessingResult result = service.process(file);

        assertThat(result.getAiTags()).containsExactly("playa", "atardecer");
    }

    @Test
    void noLlamaAGeminiSiElProcesamientoFallo() {
        when(processor.process(file)).thenReturn(
                ProcessingResult.failure(file.getId(), file.getOwnerId(), "archivo corrupto"));

        ProcessingResult result = service.process(file);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getAiTags()).isEmpty();
        verifyNoInteractions(tagger);
    }
}
