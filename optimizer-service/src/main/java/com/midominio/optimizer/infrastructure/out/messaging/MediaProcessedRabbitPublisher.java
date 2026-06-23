package com.midominio.optimizer.infrastructure.out.messaging;

import com.midominio.optimizer.domain.model.ProcessingResult;
import com.midominio.optimizer.domain.port.out.ResultPublisher;
import com.midominio.optimizer.infrastructure.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class MediaProcessedRabbitPublisher implements ResultPublisher {

    private final RabbitTemplate rabbitTemplate;

    public MediaProcessedRabbitPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishProcessed(ProcessingResult result) {
        var message = new MediaProcessedMessage(
                result.getFileId(),
                result.getOwnerId(),
                result.getDerivatives(),
                result.getMetadata(),
                result.isSuccess(),
                result.getErrorMessage());
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.PROCESSED_RK, message);
    }

    record MediaProcessedMessage(
            UUID fileId,
            UUID ownerId,
            Map<String, String> derivatives,
            Map<String, Object> metadata,
            boolean success,
            String errorMessage) {}
}
