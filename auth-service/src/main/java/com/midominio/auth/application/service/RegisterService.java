package com.midominio.auth.application.service;

import com.midominio.auth.domain.exception.EmailAlreadyRegisteredException;
import com.midominio.auth.domain.model.User;
import com.midominio.auth.domain.port.in.RegisterCommand;
import com.midominio.auth.domain.port.in.RegisterUseCase;
import com.midominio.auth.domain.port.out.PasswordHasher;
import com.midominio.auth.domain.port.out.UserRepository;
import org.springframework.transaction.annotation.Transactional;

public class RegisterService implements RegisterUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public RegisterService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    @Transactional
    public void register(RegisterCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new EmailAlreadyRegisteredException(command.email());
        }
        User user = User.create(command.email(), passwordHasher.hash(command.rawPassword()));
        userRepository.save(user);
    }
}
