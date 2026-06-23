package com.midominio.app.domain.port.in;

import java.util.UUID;

public record CreateTagCommand(String name, UUID ownerId) {}
