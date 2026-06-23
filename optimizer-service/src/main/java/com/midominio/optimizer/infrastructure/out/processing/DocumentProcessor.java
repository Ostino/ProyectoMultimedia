package com.midominio.optimizer.infrastructure.out.processing;

import com.midominio.optimizer.domain.model.FileType;
import com.midominio.optimizer.domain.model.MediaFile;
import com.midominio.optimizer.domain.model.ProcessingResult;
import com.midominio.optimizer.domain.port.out.MediaProcessor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class DocumentProcessor implements MediaProcessor {

    private static final Logger log = LoggerFactory.getLogger(DocumentProcessor.class);

    @Override
    public boolean supports(FileType fileType) {
        return fileType == FileType.DOCUMENT;
    }

    @Override
    public ProcessingResult process(MediaFile file) {
        try {
            Path source     = Path.of(file.getStoragePath());
            Path previewDir = source.getParent().resolve("preview");
            Files.createDirectories(previewDir);

            Map<String, String> derivatives = new LinkedHashMap<>();
            Map<String, Object> metadata    = new LinkedHashMap<>();

            try (PDDocument document = Loader.loadPDF(source.toFile())) {
                metadata.put("pageCount", document.getNumberOfPages());

                PDDocumentInformation info = document.getDocumentInformation();
                if (info.getTitle()  != null) metadata.put("title",  info.getTitle());
                if (info.getAuthor() != null) metadata.put("author", info.getAuthor());
                if (info.getSubject()!= null) metadata.put("subject",info.getSubject());

                // First page as JPEG
                PDFRenderer renderer = new PDFRenderer(document);
                BufferedImage image  = renderer.renderImageWithDPI(0, 150);
                Path preview = previewDir.resolve("preview.jpg");
                ImageIO.write(image, "jpg", preview.toFile());
                derivatives.put("preview", preview.toString());
            }

            return ProcessingResult.success(file.getId(), file.getOwnerId(), derivatives, metadata);

        } catch (Exception e) {
            log.error("Document processing failed for {}", file.getId(), e);
            return ProcessingResult.failure(file.getId(), file.getOwnerId(), e.getMessage());
        }
    }
}
