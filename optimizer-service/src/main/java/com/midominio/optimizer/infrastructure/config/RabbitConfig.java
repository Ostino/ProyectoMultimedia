package com.midominio.optimizer.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE     = "media.exchange";
    public static final String QUEUE        = "media.uploaded";
    public static final String DLX          = "media.dlx";
    public static final String DLQ          = "media.uploaded.dlq";
    public static final String DLQ_RK       = "media.uploaded.dlq";
    public static final String PROCESSED_RK = "media.processed";

    @Bean
    public TopicExchange mediaExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
    }

    /** Queue principal con dead-letter hacia media.dlx */
    @Bean
    public Queue mediaUploadedQueue() {
        return QueueBuilder.durable(QUEUE)
                .deadLetterExchange(DLX)
                .deadLetterRoutingKey(DLQ_RK)
                .build();
    }

    @Bean
    public Binding mediaUploadedBinding(Queue mediaUploadedQueue, TopicExchange mediaExchange) {
        return BindingBuilder.bind(mediaUploadedQueue).to(mediaExchange).with(QUEUE);
    }

    /** Dead-letter infrastructure */
    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(DLX).durable(true).build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(DLQ_RK);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
