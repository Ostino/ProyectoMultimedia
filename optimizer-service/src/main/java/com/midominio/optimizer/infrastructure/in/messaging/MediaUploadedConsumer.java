package com.midominio.optimizer.infrastructure.in.messaging;

import com.midominio.optimizer.domain.model.FileType;
import com.midominio.optimizer.domain.model.MediaFile;
import com.midominio.optimizer.domain.model.ProcessingResult;
import com.midominio.optimizer.domain.port.in.ProcessMediaUseCase;
import com.midominio.optimizer.domain.port.out.ResultPublisher;
import com.midominio.optimizer.infrastructure.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MediaUploadedConsumer {

    private static final Logger log = LoggerFactory.getLogger(MediaUploadedConsumer.class);

    private final ProcessMediaUseCase processMediaUseCase;
    private final ResultPublisher resultPublisher;

    public MediaUploadedConsumer(ProcessMediaUseCase processMediaUseCase,
                                 ResultPublisher resultPublisher) {
        this.processMediaUseCase = processMediaUseCase;
        this.resultPublisher     = resultPublisher;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onMediaUploaded(MediaUploadedMessage message) {
        log.info("Received media.uploaded for fileId={}", message.fileId());
        try {
            MediaFile file = new MediaFile(
                    message.fileId(),
                    message.ownerId(),
                    message.storagePath(),
                    FileType.valueOf(message.fileType()));

            ProcessingResult result = processMediaUseCase.process(file);
            resultPublisher.publishProcessed(result);
            log.info("Processing complete for fileId={} success={}", file.getId(), result.isSuccess());
        } catch (Exception e) {
            log.error("Processing failed for fileId={}, routing to DLQ", message.fileId(), e);
            throw new AmqpRejectAndDontRequeueException("Processing failed: " + e.getMessage(), e);
        }
    }
}
