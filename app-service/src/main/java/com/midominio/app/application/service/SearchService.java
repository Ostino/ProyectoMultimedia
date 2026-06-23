package com.midominio.app.application.service;

import com.midominio.app.domain.model.MediaFile;
import com.midominio.app.domain.model.PageResult;
import com.midominio.app.domain.port.in.SearchCriteria;
import com.midominio.app.domain.port.in.SearchUseCase;
import com.midominio.app.domain.port.out.MediaFileRepository;

import java.util.UUID;

public class SearchService implements SearchUseCase {

    private final MediaFileRepository mediaFileRepository;

    public SearchService(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    @Override
    public PageResult<MediaFile> search(SearchCriteria criteria, UUID ownerId,
                                        int page, int size, String sortBy, boolean asc) {
        return mediaFileRepository.search(criteria, ownerId, page, size, sortBy, asc);
    }
}
