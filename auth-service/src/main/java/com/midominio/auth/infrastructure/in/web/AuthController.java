package com.midominio.auth.infrastructure.in.web;

import com.midominio.auth.domain.port.in.LoginCommand;
import com.midominio.auth.domain.port.in.LoginUseCase;
import com.midominio.auth.domain.port.in.RegisterCommand;
import com.midominio.auth.domain.port.in.RegisterUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;

    public AuthController(RegisterUseCase registerUseCase, LoginUseCase loginUseCase) {
        this.registerUseCase = registerUseCase;
        this.loginUseCase    = loginUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        registerUseCase.register(new RegisterCommand(request.email(), request.password()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = loginUseCase.login(new LoginCommand(request.email(), request.password()));
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
