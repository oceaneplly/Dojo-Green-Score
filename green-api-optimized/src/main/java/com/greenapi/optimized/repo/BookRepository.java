package com.greenapi.optimized.repo;

import com.greenapi.optimized.domain.Book;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

@Repository
public class BookRepository {
    private final List<Book> data;
    private final long bookNumber;

    public BookRepository(@Value("${BOOK_NUMBER}") long bookNumber) {
        this.bookNumber = bookNumber;
        this.data = new ArrayList<>();
        LongStream.rangeClosed(1, bookNumber).forEach(i ->
                data.add(new Book(
                        i,
                        "Title " + i,
                        "Author " + ((i % 20) + 1),
                        1990 + (int)(i % 30),
                        100 + (int)(i % 400),
                        "Long summary to inflate payload " + "x".repeat(2)
                ))
        );
    }

    public List<Book> findAll() { return data; }

    public List<Book> findByPage(int page, int size) {
        int start = page * size;
        int end = Math.min(start + size, (int) data.size());

        if (start >= data.size()) {
            return new ArrayList<>();
        }

        return data.subList(start, end);
    }

    public List<Book> findByLastModifiedAfter(long timestamp) {
        return data.stream()
                .filter(book -> book.getLastModified() > timestamp)
                .toList();
    }

    public Book findById(long id) { return data.stream().filter(b -> b.getId() == id).findFirst().orElse(null); }
    public long getBookNumber() { return bookNumber; }
    public long getTotalElements() { return data.size(); }
}
