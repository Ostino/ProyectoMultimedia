package com.midominio.app.domain.port.out;

import com.midominio.app.domain.model.MediaUploadedEvent;

public interface MediaEventPublisher {
    void publishUploaded(MediaUploadedEvent event);
}
