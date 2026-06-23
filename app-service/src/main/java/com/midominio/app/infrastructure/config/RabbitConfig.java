package com.midominio.app.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE        = "media.exchange";
    public static final String QUEUE           = "media.uploaded";
    public static final String ROUTING_KEY     = "media.uploaded";
    public static final String PROCESSED_QUEUE = "media.processed";
    public static final String DLQ             = "media.uploaded.dlq";
    // Must match optimizer-service declaration so RabbitMQ doesn't reject the redeclaration
    private static final String DLX            = "media.dlx";

    @Bean
    public TopicExchange mediaExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue mediaUploadedQueue() {
        return QueueBuilder.durable(QUEUE)
                .deadLetterExchange(DLX)
                .deadLetterRoutingKey(DLQ)
                .build();
    }

    @Bean
    public Binding mediaUploadedBinding(Queue mediaUploadedQueue, TopicExchange mediaExchange) {
        return BindingBuilder.bind(mediaUploadedQueue).to(mediaExchange).with(ROUTING_KEY);
    }

    @Bean
    public Queue mediaProcessedQueue() {
        return QueueBuilder.durable(PROCESSED_QUEUE).build();
    }

    @Bean
    public Binding mediaProcessedBinding(Queue mediaProcessedQueue, TopicExchange mediaExchange) {
        return BindingBuilder.bind(mediaProcessedQueue).to(mediaExchange).with("media.processed");
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(DLX).durable(true).build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Binding dlqBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(DLQ);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
