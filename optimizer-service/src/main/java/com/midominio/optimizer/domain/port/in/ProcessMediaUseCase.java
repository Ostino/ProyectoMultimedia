package com.midominio.optimizer.domain.port.in;

import com.midominio.optimizer.domain.model.MediaFile;
import com.midominio.optimizer.domain.model.ProcessingResult;

public interface ProcessMediaUseCase {
    ProcessingResult process(MediaFile file);
}
