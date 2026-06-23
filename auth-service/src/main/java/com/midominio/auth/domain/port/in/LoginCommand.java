package com.midominio.auth.domain.port.in;

public record LoginCommand(String email, String rawPassword) {}
