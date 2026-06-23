package com.midominio.app.infrastructure.in.messaging;

import com.midominio.app.domain.port.in.HandleMediaFailedUseCase;
import com.midominio.app.infrastructure.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DlqConsumer {

    private static final Logger log = LoggerFactory.getLogger(DlqConsumer.class);

    private final HandleMediaFailedUseCase handleFailed;

    public DlqConsumer(HandleMediaFailedUseCase handleFailed) {
        this.handleFailed = handleFailed;
    }

    /**
     * Receives the original MediaUploadedEvent that optimizer-service rejected.
     * RabbitMQ preserves the original body on dead-lettering.
     */
    @RabbitListener(queues = RabbitConfig.DLQ)
    public void onDeadLetter(MediaUploadedDlqMessage message) {
        log.error("DLQ: processing permanently failed for fileId={}", message.fileId());
        handleFailed.handle(message.fileId());
    }

    record MediaUploadedDlqMessage(UUID fileId, UUID ownerId, String storagePath, String fileType) {}
}
