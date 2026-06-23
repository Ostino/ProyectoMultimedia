package com.midominio.app.domain.port.out;

public interface MimeTypeDetector {
    String detect(byte[] content, String filename);
}
