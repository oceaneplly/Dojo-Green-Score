package com.greenapi.optimized.api;

import com.greenapi.optimized.domain.Book;
import java.util.List;

public record PaginatedResponse(
        List<Book> content,
        int page,
        int size,
        long totalElements,
        long totalPages
) {}

