package com.midominio.app.infrastructure.in.messaging;

import com.midominio.app.domain.port.in.HandleMediaFailedUseCase;
import com.midominio.app.domain.port.in.HandleMediaProcessedCommand;
import com.midominio.app.domain.port.in.HandleMediaProcessedUseCase;
import com.midominio.app.domain.port.in.TagUseCase;
import com.midominio.app.infrastructure.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MediaProcessedConsumer {

    private static final Logger log = LoggerFactory.getLogger(MediaProcessedConsumer.class);

    private final HandleMediaProcessedUseCase handleProcessed;
    private final HandleMediaFailedUseCase    handleFailed;
    private final TagUseCase                  tagUseCase;

    public MediaProcessedConsumer(HandleMediaProcessedUseCase handleProcessed,
                                  HandleMediaFailedUseCase handleFailed,
                                  TagUseCase tagUseCase) {
        this.handleProcessed = handleProcessed;
        this.handleFailed    = handleFailed;
        this.tagUseCase      = tagUseCase;
    }

    @RabbitListener(queues = RabbitConfig.PROCESSED_QUEUE)
    public void onMediaProcessed(MediaProcessedMessage message) {
        log.info("Received media.processed for fileId={} success={}", message.fileId(), message.success());
        if (message.success()) {
            handleProcessed.handle(new HandleMediaProcessedCommand(
                    message.fileId(), message.derivatives(), message.metadata()));
            applyAiTags(message);
        } else {
            log.warn("Processing reported failure for fileId={}: {}", message.fileId(), message.errorMessage());
            handleFailed.handle(message.fileId());
        }
    }

    /**
     * Las etiquetas de IA se guardan en una transacción aparte, después de marcar
     * el archivo como READY: si fallan, el archivo queda procesado sin ellas.
     */
    private void applyAiTags(MediaProcessedMessage message) {
        if (message.aiTags() == null || message.aiTags().isEmpty()) return;
        try {
            tagUseCase.applyAutoTags(message.fileId(), message.ownerId(), message.aiTags());
        } catch (RuntimeException e) {
            log.warn("No se pudieron guardar las etiquetas de IA de fileId={}: {}", message.fileId(), e.getMessage());
        }
    }
}
