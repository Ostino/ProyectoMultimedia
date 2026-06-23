package com.midominio.app.domain.model;

import java.util.List;

public record PageResult<T>(List<T> content, long totalElements, int totalPages, int page, int size) {}
