package com.midominio.app.domain.model;

import java.util.List;
import java.util.Optional;

public record FileDetail(MediaFile file, Optional<Metadata> metadata, List<Thumbnail> derivatives) {}
