package com.midominio.optimizer.application.service;

import com.midominio.optimizer.domain.model.MediaFile;
import com.midominio.optimizer.domain.model.ProcessingResult;
import com.midominio.optimizer.domain.port.in.ProcessMediaUseCase;
import com.midominio.optimizer.domain.port.out.MediaProcessor;

import java.util.List;

public class ProcessMediaService implements ProcessMediaUseCase {

    private final List<MediaProcessor> processors;

    public ProcessMediaService(List<MediaProcessor> processors) {
        this.processors = processors;
    }

    @Override
    public ProcessingResult process(MediaFile file) {
        return processors.stream()
                .filter(p -> p.supports(file.getFileType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No processor registered for file type: " + file.getFileType()))
                .process(file);
    }
}
