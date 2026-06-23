package com.midominio.auth.domain.port.in;

public record RegisterCommand(String email, String rawPassword) {}
