package com.midominio.optimizer.infrastructure.out.processing;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.midominio.optimizer.domain.model.FileType;
import com.midominio.optimizer.domain.model.MediaFile;
import com.midominio.optimizer.domain.model.ProcessingResult;
import com.midominio.optimizer.domain.port.out.MediaProcessor;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ImageProcessor implements MediaProcessor {

    private static final Logger log = LoggerFactory.getLogger(ImageProcessor.class);
    private static final int[] THUMBNAIL_SIZES = {128, 512, 1024};

    @Override
    public boolean supports(FileType fileType) {
        return fileType == FileType.IMAGE;
    }

    @Override
    public ProcessingResult process(MediaFile file) {
        try {
            Path source  = Path.of(file.getStoragePath());
            Path thumbDir = source.getParent().resolve("thumbnails");
            Files.createDirectories(thumbDir);

            Map<String, String> derivatives = new LinkedHashMap<>();
            for (int size : THUMBNAIL_SIZES) {
                Path thumb = thumbDir.resolve("thumb_" + size + ".jpg");
                Thumbnails.of(source.toFile())
                        .size(size, size)
                        .keepAspectRatio(true)
                        .outputFormat("jpg")
                        .toFile(thumb.toFile());
                derivatives.put("thumb_" + size, thumb.toString());
            }

            Map<String, Object> metadata = extractExif(source.toFile());
            return ProcessingResult.success(file.getId(), file.getOwnerId(), derivatives, metadata);

        } catch (Exception e) {
            log.error("Image processing failed for {}", file.getId(), e);
            return ProcessingResult.failure(file.getId(), file.getOwnerId(), e.getMessage());
        }
    }

    private Map<String, Object> extractExif(File source) {
        Map<String, Object> meta = new LinkedHashMap<>();
        try {
            Metadata exif = ImageMetadataReader.readMetadata(source);

            ExifIFD0Directory ifd0 = exif.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (ifd0 != null) {
                meta.put("make",  ifd0.getDescription(ExifIFD0Directory.TAG_MAKE));
                meta.put("model", ifd0.getDescription(ExifIFD0Directory.TAG_MODEL));
            }
            ExifSubIFDDirectory sub = exif.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (sub != null) {
                meta.put("dateCaptured", sub.getDescription(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
            }
            JpegDirectory jpeg = exif.getFirstDirectoryOfType(JpegDirectory.class);
            if (jpeg != null) {
                meta.put("width",  jpeg.getImageWidth());
                meta.put("height", jpeg.getImageHeight());
            }
        } catch (Exception e) {
            log.warn("Could not extract EXIF from {}: {}", source.getName(), e.getMessage());
        }
        return meta;
    }
}
