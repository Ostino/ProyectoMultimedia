package com.midominio.app.domain.port.in;

import com.midominio.app.domain.model.MediaFile;
import com.midominio.app.domain.model.PageResult;

import java.util.UUID;

public interface SearchUseCase {
    PageResult<MediaFile> search(SearchCriteria criteria, UUID ownerId,
                                 int page, int size, String sortBy, boolean asc);
}
