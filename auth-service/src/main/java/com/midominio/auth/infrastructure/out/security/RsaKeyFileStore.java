package com.midominio.auth.infrastructure.out.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Guarda el par de llaves RSA que firma los JWT en un directorio, para que
 * sobreviva a reinicios y redespliegues (si no, cada arranque invalidaría
 * todas las sesiones).
 *
 * <ul>
 *   <li>{@code jwt-private.pem}: llave privada en PKCS#8</li>
 *   <li>{@code jwt-public.pem}: llave pública en X.509 (SubjectPublicKeyInfo)</li>
 * </ul>
 *
 * El {@code kid} es el thumbprint RFC 7638 de la llave pública, así que es
 * estable mientras la llave no cambie.
 */
public class RsaKeyFileStore {

    private static final Logger log = LoggerFactory.getLogger(RsaKeyFileStore.class);

    static final String PRIVATE_FILE = "jwt-private.pem";
    static final String PUBLIC_FILE  = "jwt-public.pem";
    private static final int KEY_SIZE = 2048;

    private final Path dir;

    public RsaKeyFileStore(Path dir) {
        this.dir = dir;
    }

    /** Carga el par de llaves del directorio, o lo genera y lo guarda si no existe. */
    public RSAKey loadOrCreate() {
        Path privatePath = dir.resolve(PRIVATE_FILE);
        Path publicPath  = dir.resolve(PUBLIC_FILE);
        boolean hasPrivate = Files.exists(privatePath);
        boolean hasPublic  = Files.exists(publicPath);

        try {
            if (hasPrivate && hasPublic) {
                RSAKey key = load(privatePath, publicPath);
                log.info("Llave RSA cargada desde {} (kid={})", dir, key.getKeyID());
                return key;
            }
            if (hasPrivate || hasPublic) {
                throw new IllegalStateException("Falta " + (hasPrivate ? PUBLIC_FILE : PRIVATE_FILE)
                        + " en " + dir + ". Restaura el archivo o borra ambos para generar un par nuevo.");
            }
            RSAKey key = generateAndStore(privatePath, publicPath);
            log.info("Llave RSA nueva generada y guardada en {} (kid={})", dir, key.getKeyID());
            return key;
        } catch (IOException | GeneralSecurityException | JOSEException e) {
            throw new IllegalStateException("No se pudo cargar ni crear la llave RSA en " + dir, e);
        }
    }

    private RSAKey load(Path privatePath, Path publicPath) throws IOException, GeneralSecurityException, JOSEException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKey privateKey = (RSAPrivateKey) kf.generatePrivate(
                new PKCS8EncodedKeySpec(readPem(privatePath)));
        RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(
                new X509EncodedKeySpec(readPem(publicPath)));

        if (!privateKey.getModulus().equals(publicKey.getModulus())) {
            throw new IllegalStateException(PRIVATE_FILE + " y " + PUBLIC_FILE + " en " + dir + " no forman un par");
        }
        return toJwk(publicKey, privateKey);
    }

    private RSAKey generateAndStore(Path privatePath, Path publicPath)
            throws IOException, GeneralSecurityException, JOSEException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(KEY_SIZE);
        KeyPair pair = generator.generateKeyPair();

        Files.createDirectories(dir);
        // La pública se escribe al final: si el proceso se corta antes, el
        // siguiente arranque lo detecta (falta un archivo) en vez de usar un par incompleto.
        writePemAtomically(privatePath, "PRIVATE KEY", pair.getPrivate().getEncoded(), true);
        writePemAtomically(publicPath, "PUBLIC KEY", pair.getPublic().getEncoded(), false);

        return toJwk((RSAPublicKey) pair.getPublic(), (RSAPrivateKey) pair.getPrivate());
    }

    private static RSAKey toJwk(RSAPublicKey publicKey, RSAPrivateKey privateKey) throws JOSEException {
        RSAKey withoutKid = new RSAKey.Builder(publicKey).build();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(withoutKid.computeThumbprint().toString())
                .build();
    }

    private static byte[] readPem(Path path) throws IOException {
        String base64 = Files.readString(path, StandardCharsets.US_ASCII)
                .replaceAll("-----(BEGIN|END) [A-Z ]+-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(base64);
    }

    private static void writePemAtomically(Path target, String type, byte[] der, boolean ownerOnly)
            throws IOException {
        String pem = "-----BEGIN " + type + "-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.US_ASCII)).encodeToString(der)
                + "\n-----END " + type + "-----\n";

        Path tmp = Files.createTempFile(target.getParent(), target.getFileName().toString(), ".tmp");
        try {
            if (ownerOnly) restrictToOwner(tmp);
            Files.writeString(tmp, pem, StandardCharsets.US_ASCII);
            Files.move(tmp, target, StandardCopyOption.ATOMIC_MOVE);
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    private static void restrictToOwner(Path path) {
        try {
            Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rw-------"));
        } catch (UnsupportedOperationException | IOException e) {
            // Sistemas sin permisos POSIX (Windows): se deja como está
        }
    }
}
