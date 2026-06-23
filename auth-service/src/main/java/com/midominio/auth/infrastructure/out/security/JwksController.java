package com.midominio.auth.infrastructure.out.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class JwksController {

    private final Map<String, Object> jwks;

    public JwksController(RSAKey rsaKey) {
        this.jwks = new JWKSet(rsaKey.toPublicJWK()).toJSONObject();
    }

    @GetMapping(value = "/.well-known/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> jwks() {
        return jwks;
    }
}
