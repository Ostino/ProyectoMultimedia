package com.midominio.auth.infrastructure.config;

import com.midominio.auth.application.service.LoginService;
import com.midominio.auth.application.service.RegisterService;
import com.midominio.auth.domain.port.in.LoginUseCase;
import com.midominio.auth.domain.port.in.RegisterUseCase;
import com.midominio.auth.domain.port.out.PasswordHasher;
import com.midominio.auth.domain.port.out.TokenSigner;
import com.midominio.auth.domain.port.out.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBeans {

    @Bean
    public RegisterUseCase registerUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        return new RegisterService(userRepository, passwordHasher);
    }

    @Bean
    public LoginUseCase loginUseCase(UserRepository userRepository, PasswordHasher passwordHasher, TokenSigner tokenSigner) {
        return new LoginService(userRepository, passwordHasher, tokenSigner);
    }
}
