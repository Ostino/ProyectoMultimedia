package com.midominio.app.infrastructure.config;

import com.midominio.app.application.service.*;
import com.midominio.app.domain.port.in.*;
import com.midominio.app.domain.port.out.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBeans {

    @Bean
    public FolderUseCase folderUseCase(FolderRepository folderRepository) {
        return new FolderService(folderRepository);
    }

    @Bean
    public UploadFileUseCase uploadFileUseCase(MediaFileRepository mediaFileRepository,
                                               StorageService storageService,
                                               MimeTypeDetector mimeTypeDetector,
                                               MediaEventPublisher mediaEventPublisher) {
        return new UploadFileService(mediaFileRepository, storageService, mimeTypeDetector, mediaEventPublisher);
    }

    @Bean
    public HandleMediaProcessedUseCase handleMediaProcessedUseCase(
            MediaFileRepository mediaFileRepository,
            MetadataRepository metadataRepository,
            ThumbnailRepository thumbnailRepository) {
        return new HandleMediaProcessedService(mediaFileRepository, metadataRepository, thumbnailRepository);
    }

    @Bean
    public HandleMediaFailedUseCase handleMediaFailedUseCase(MediaFileRepository mediaFileRepository) {
        return new HandleMediaFailedService(mediaFileRepository);
    }

    @Bean
    public FileQueryUseCase fileQueryUseCase(MediaFileRepository mediaFileRepository,
                                             MetadataRepository metadataRepository,
                                             ThumbnailRepository thumbnailRepository) {
        return new FileQueryService(mediaFileRepository, metadataRepository, thumbnailRepository);
    }

    @Bean
    public TagUseCase tagUseCase(TagRepository tagRepository, MediaFileRepository mediaFileRepository) {
        return new TagService(tagRepository, mediaFileRepository);
    }

    @Bean
    public SearchUseCase searchUseCase(MediaFileRepository mediaFileRepository) {
        return new SearchService(mediaFileRepository);
    }
}
