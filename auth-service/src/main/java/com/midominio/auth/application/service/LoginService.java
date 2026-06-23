package com.midominio.auth.application.service;

import com.midominio.auth.domain.exception.InvalidCredentialsException;
import com.midominio.auth.domain.port.in.LoginCommand;
import com.midominio.auth.domain.port.in.LoginUseCase;
import com.midominio.auth.domain.port.out.PasswordHasher;
import com.midominio.auth.domain.port.out.TokenSigner;
import com.midominio.auth.domain.port.out.UserRepository;

public class LoginService implements LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenSigner tokenSigner;

    public LoginService(UserRepository userRepository, PasswordHasher passwordHasher, TokenSigner tokenSigner) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenSigner = tokenSigner;
    }

    @Override
    public String login(LoginCommand command) {
        var user = userRepository.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHasher.matches(command.rawPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return tokenSigner.sign(user.getId(), user.getEmail());
    }
}
