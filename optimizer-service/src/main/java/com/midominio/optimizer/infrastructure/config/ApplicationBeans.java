package com.midominio.optimizer.infrastructure.config;

import com.midominio.optimizer.application.service.ProcessMediaService;
import com.midominio.optimizer.domain.port.in.ProcessMediaUseCase;
import com.midominio.optimizer.domain.port.out.AutoTagger;
import com.midominio.optimizer.domain.port.out.MediaProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(GeminiProperties.class)
public class ApplicationBeans {

    @Bean
    public ProcessMediaUseCase processMediaUseCase(List<MediaProcessor> processors, AutoTagger autoTagger) {
        return new ProcessMediaService(processors, autoTagger);
    }
}
