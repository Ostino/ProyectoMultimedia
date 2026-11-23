package com.midominio.optimizer.application.service;

import com.midominio.optimizer.domain.model.MediaFile;
import com.midominio.optimizer.domain.model.ProcessingResult;
import com.midominio.optimizer.domain.port.in.ProcessMediaUseCase;
import com.midominio.optimizer.domain.port.out.AutoTagger;
import com.midominio.optimizer.domain.port.out.MediaProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProcessMediaService implements ProcessMediaUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessMediaService.class);

    private final List<MediaProcessor> processors;
    private final AutoTagger autoTagger;

    public ProcessMediaService(List<MediaProcessor> processors, AutoTagger autoTagger) {
        this.processors = processors;
        this.autoTagger = autoTagger;
    }

    @Override
    public ProcessingResult process(MediaFile file) {
        ProcessingResult result = processors.stream()
                .filter(p -> p.supports(file.getFileType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No processor registered for file type: " + file.getFileType()))
                .process(file);

        if (!result.isSuccess()) return result;
        return result.withAiTags(suggestTagsSafely(file, result));
    }

    /**
     * El etiquetado con IA es opcional: cualquier fallo del proveedor (sin API key,
     * cuota agotada, timeout, 5xx...) deja el archivo sin etiquetas automáticas,
     * pero nunca hace fallar el procesamiento.
     */
    private List<String> suggestTagsSafely(MediaFile file, ProcessingResult result) {
        try {
            List<String> tags = autoTagger.suggestTags(file, result);
            return tags == null ? List.of() : tags;
        } catch (RuntimeException e) {
            log.warn("Etiquetado con IA no disponible para fileId={}; se guarda sin etiquetas automáticas: {}",
                    file.getId(), e.getMessage());
            return List.of();
        }
    }
}
