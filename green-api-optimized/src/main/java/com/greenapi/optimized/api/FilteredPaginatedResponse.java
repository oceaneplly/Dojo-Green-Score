package com.greenapi.optimized.api;

import java.util.List;

public record FilteredPaginatedResponse(
        List<BookDTO> content,
        int page,
        int size,
        long totalElements,
        long totalPages
) {}

