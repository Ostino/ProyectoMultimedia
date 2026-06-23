package com.midominio.app.infrastructure.in.messaging;

import com.midominio.app.domain.port.in.HandleMediaFailedUseCase;
import com.midominio.app.domain.port.in.HandleMediaProcessedCommand;
import com.midominio.app.domain.port.in.HandleMediaProcessedUseCase;
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

    public MediaProcessedConsumer(HandleMediaProcessedUseCase handleProcessed,
                                  HandleMediaFailedUseCase handleFailed) {
        this.handleProcessed = handleProcessed;
        this.handleFailed    = handleFailed;
    }

    @RabbitListener(queues = RabbitConfig.PROCESSED_QUEUE)
    public void onMediaProcessed(MediaProcessedMessage message) {
        log.info("Received media.processed for fileId={} success={}", message.fileId(), message.success());
        if (message.success()) {
            handleProcessed.handle(new HandleMediaProcessedCommand(
                    message.fileId(), message.derivatives(), message.metadata()));
        } else {
            log.warn("Processing reported failure for fileId={}: {}", message.fileId(), message.errorMessage());
            handleFailed.handle(message.fileId());
        }
    }
}
