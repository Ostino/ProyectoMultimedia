package com.midominio.auth.infrastructure.out.persistence;

import com.midominio.auth.domain.model.User;
import com.midominio.auth.domain.port.out.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpa;

    public UserRepositoryAdapter(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(User user) {
        jpa.save(new UserJpaEntity(user.getId(), user.getEmail(), user.getPasswordHash(), user.getCreatedAt()));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpa.findByEmail(email)
                .map(e -> new User(e.getId(), e.getEmail(), e.getPasswordHash(), e.getCreatedAt()));
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }
}
