package com.midominio.app.infrastructure.out.messaging;

import com.midominio.app.domain.model.MediaUploadedEvent;
import com.midominio.app.domain.port.out.MediaEventPublisher;
import com.midominio.app.infrastructure.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MediaEventRabbitPublisher implements MediaEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public MediaEventRabbitPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishUploaded(MediaUploadedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                event);
    }
}
