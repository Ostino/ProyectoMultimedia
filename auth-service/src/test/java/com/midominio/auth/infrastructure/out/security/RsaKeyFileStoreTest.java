package com.midominio.auth.infrastructure.out.security;

import com.nimbusds.jose.jwk.RSAKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RsaKeyFileStoreTest {

    @TempDir
    Path dir;

    @Test
    void laPrimeraCargaGeneraLosArchivosYLaSegundaDevuelveLaMismaLlave() throws Exception {
        RSAKey first = new RsaKeyFileStore(dir).loadOrCreate();

        assertThat(dir.resolve(RsaKeyFileStore.PRIVATE_FILE)).exists();
        assertThat(dir.resolve(RsaKeyFileStore.PUBLIC_FILE)).exists();
        assertThat(first.isPrivate()).isTrue();

        RSAKey second = new RsaKeyFileStore(dir).loadOrCreate();

        assertThat(second.toRSAPublicKey()).isEqualTo(first.toRSAPublicKey());
        assertThat(second.toRSAPrivateKey().getEncoded()).isEqualTo(first.toRSAPrivateKey().getEncoded());
        assertThat(second.getKeyID()).isEqualTo(first.getKeyID());
    }

    @Test
    void elKidSeDerivaDeLaLlavePublica() throws Exception {
        RSAKey key = new RsaKeyFileStore(dir).loadOrCreate();

        assertThat(key.getKeyID()).isEqualTo(key.toPublicJWK().computeThumbprint().toString());
    }

    @Test
    void fallaSiFaltaUnoDeLosDosArchivos() throws Exception {
        new RsaKeyFileStore(dir).loadOrCreate();
        Files.delete(dir.resolve(RsaKeyFileStore.PUBLIC_FILE));

        assertThatThrownBy(() -> new RsaKeyFileStore(dir).loadOrCreate())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(RsaKeyFileStore.PUBLIC_FILE);
    }
}
