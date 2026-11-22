package com.midominio.auth.infrastructure.config;

import com.midominio.auth.infrastructure.out.security.RsaKeyFileStore;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class RsaKeyConfig {

    /**
     * Par de llaves que firma los JWT. Se guarda en JWT_KEYS_DIR (por defecto /keys)
     * para que los tokens sigan siendo válidos después de reiniciar el servicio.
     */
    @Bean
    public RSAKey rsaKey(@Value("${jwt.keys-dir:/keys}") String keysDir) {
        return new RsaKeyFileStore(Path.of(keysDir)).loadOrCreate();
    }
}
