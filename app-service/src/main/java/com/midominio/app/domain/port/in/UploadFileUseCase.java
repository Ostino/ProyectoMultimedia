package com.midominio.app.domain.port.in;

import com.midominio.app.domain.model.MediaFile;

public interface UploadFileUseCase {
    MediaFile upload(UploadFileCommand command);
}
