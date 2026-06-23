package com.midominio.app.domain.port.out;

import com.midominio.app.domain.model.FileStatus;
import com.midominio.app.domain.model.MediaFile;
import com.midominio.app.domain.model.PageResult;
import com.midominio.app.domain.port.in.SearchCriteria;

import java.util.Optional;
import java.util.UUID;

public interface MediaFileRepository {
    MediaFile             save(MediaFile mediaFile);
    Optional<MediaFile>   findByIdAndOwnerId(UUID id, UUID ownerId);
    void                  updateStatus(UUID fileId, FileStatus status);
    PageResult<MediaFile> search(SearchCriteria criteria, UUID ownerId,
                                 int page, int size, String sortBy, boolean asc);
}
