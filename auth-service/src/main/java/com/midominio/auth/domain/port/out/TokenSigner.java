package com.midominio.auth.domain.port.out;

import java.util.UUID;

public interface TokenSigner {
    String sign(UUID userId, String email);
}
