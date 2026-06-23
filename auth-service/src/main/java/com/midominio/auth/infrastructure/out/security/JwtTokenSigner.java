package com.midominio.auth.infrastructure.out.security;

import com.midominio.auth.domain.port.out.TokenSigner;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenSigner implements TokenSigner {

    private static final long TTL_MS = 3_600_000L;

    private final RSASSASigner signer;
    private final String keyId;

    public JwtTokenSigner(RSAKey rsaKey) throws Exception {
        this.signer = new RSASSASigner(rsaKey);
        this.keyId  = rsaKey.getKeyID();
    }

    @Override
    public String sign(UUID userId, String email) {
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(userId.toString())
                    .claim("email", email)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + TTL_MS))
                    .build();

            SignedJWT jwt = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(keyId).build(),
                    claims);
            jwt.sign(signer);
            return jwt.serialize();
        } catch (Exception e) {
            throw new RuntimeException("JWT signing failed", e);
        }
    }
}