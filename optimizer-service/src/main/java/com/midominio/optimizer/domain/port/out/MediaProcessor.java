package com.midominio.optimizer.domain.port.out;

import com.midominio.optimizer.domain.model.FileType;
import com.midominio.optimizer.domain.model.MediaFile;
import com.midominio.optimizer.domain.model.ProcessingResult;

public interface MediaProcessor {
    boolean supports(FileType fileType);
    ProcessingResult process(MediaFile file);
}
