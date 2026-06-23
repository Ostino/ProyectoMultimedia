package com.midominio.optimizer.domain.port.out;

import com.midominio.optimizer.domain.model.ProcessingResult;

public interface ResultPublisher {
    void publishProcessed(ProcessingResult result);
}
