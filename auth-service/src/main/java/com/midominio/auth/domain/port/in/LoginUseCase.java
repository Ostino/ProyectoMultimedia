package com.midominio.auth.domain.port.in;

public interface LoginUseCase {
    String login(LoginCommand command);
}
