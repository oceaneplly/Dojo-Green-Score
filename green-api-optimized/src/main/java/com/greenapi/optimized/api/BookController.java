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
            @RequestParam(required = false) String fields) {

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

        // Si le paramètre fields est présent, retourner une réponse filtrée
        if (fields != null && !fields.isEmpty()) {
            List<BookDTO> filteredContent = content.stream()
                    .map(book -> new BookDTO(book, fields))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new FilteredPaginatedResponse(filteredContent, page, size, totalElements, totalPages));
        }

        // Sinon, retourner la réponse paginée complète
        return ResponseEntity.ok(new PaginatedResponse(content, page, size, totalElements, totalPages));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable long id,
            @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {

        Book book = repo.findById(id);

        if (book == null) {
            return ResponseEntity.notFound().build();
        }

        // Générer un ETag basé sur le hash du contenu du livre
        String eTag = "\"" + Integer.toHexString(book.toString().hashCode()) + "\"";

        // Si l'ETag correspond, retourner 304 Not Modified
        if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                    .header("ETag", eTag)
                    .build();
        }

        // Sinon, retourner 200 OK avec les données et l'ETag
        return ResponseEntity.ok()
                .header("ETag", eTag)
                .body(book);
    }

    @GetMapping("/delta")
    public ResponseEntity<?> getDelta(@RequestParam(required = true) Long timestamp) {
        // Récupérer tous les livres modifiés après le timestamp
        List<Book> modifiedBooks = repo.findByLastModifiedAfter(timestamp);

        return ResponseEntity.ok().body(modifiedBooks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable long id, @RequestBody BookUpdateRequest request) {
        Book book = repo.findById(id);

        if (book == null) {
            return ResponseEntity.notFound().build();
        }

        // Mettre à jour les champs qui sont fournis dans la requête
        if (request.getTitle() != null) {
            book.setTitle(request.getTitle());
        }
        if (request.getAuthor() != null) {
            book.setAuthor(request.getAuthor());
        }
        if (request.getPublished_date() != null) {
            book.setPublished_date(request.getPublished_date());
        }
        if (request.getPages() != null) {
            book.setPages(request.getPages());
        }
        if (request.getSummary() != null) {
            book.setSummary(request.getSummary());
        }

        book.setLastModified(System.currentTimeMillis());


        return ResponseEntity.ok().body(book);
    }

}
