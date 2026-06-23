package com.midominio.app.infrastructure.out.storage;

import com.midominio.app.domain.port.out.MimeTypeDetector;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

@Component
public class TikaMimeTypeDetector implements MimeTypeDetector {

    private final Tika tika = new Tika();

    @Override
    public String detect(byte[] content, String filename) {
        try {
            return tika.detect(content, filename);
        } catch (Exception e) {
            return "application/octet-stream";
        }
    }
}
