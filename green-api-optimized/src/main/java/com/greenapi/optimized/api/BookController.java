package com.greenapi.optimized.api;

import com.greenapi.optimized.domain.Book;
import com.greenapi.optimized.repo.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookRepository repo;

    private static final int MAX_SIZE = 100;
    private static final int DEFAULT_SIZE = 20;
    private static final String DEFAULT_SIZE_STR = "20";

    public BookController(BookRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public ResponseEntity<?> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = DEFAULT_SIZE_STR) int size,
            @RequestParam(required = false) String fields,
            @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {

        // Validation
        if (page < 0) {
            throw new IllegalArgumentException("Page must be >= 0");
        }
        if (size <= 0 || size > MAX_SIZE) {
            throw new IllegalArgumentException("Size must be between 1 and " + MAX_SIZE);
        }

        List<Book> content = repo.findByPage(page, size);
        long totalElements = repo.getTotalElements();
        long totalPages = (totalElements + size - 1) / size;

        // Créer la réponse
        Object responseBody;
        if (fields != null && !fields.isEmpty()) {
            List<BookDTO> filteredContent = content.stream()
                    .map(book -> new BookDTO(book, fields))
                    .collect(Collectors.toList());
            responseBody = new FilteredPaginatedResponse(filteredContent, page, size, totalElements, totalPages);
        } else {
            responseBody = new PaginatedResponse(content, page, size, totalElements, totalPages);
        }

        // Générer un ETag simple basé sur le hash du contenu
        String eTag = "\"" + Integer.toHexString(responseBody.toString().hashCode()) + "\"";

        // Si l'ETag correspond, retourner 304 Not Modified
        if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                    .header("ETag", eTag)
                    .build();
        }

        // Sinon, retourner 200 OK avec les données et l'ETag
        return ResponseEntity.ok()
                .header("ETag", eTag)
                .body(responseBody);
    }

}
