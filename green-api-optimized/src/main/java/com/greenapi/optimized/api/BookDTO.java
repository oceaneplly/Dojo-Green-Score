package com.greenapi.optimized.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.greenapi.optimized.domain.Book;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookDTO {
    public Long id;
    public String title;
    public String author;
    public Integer published_date;
    public Integer pages;
    public String summary;

    public BookDTO(Book book, String fieldsParam) {
        if (fieldsParam == null || fieldsParam.isEmpty()) {
            // Champs par défaut : id et title uniquement
            this.id = book.id();
            this.title = book.title();
        } else {
            // Filtrer selon les champs demandés
            String[] fields = fieldsParam.split(",");
            for (String field : fields) {
                field = field.trim();
                switch (field) {
                    case "id" -> this.id = book.id();
                    case "title" -> this.title = book.title();
                    case "author" -> this.author = book.author();
                    case "published_date" -> this.published_date = book.published_date();
                    case "pages" -> this.pages = book.pages();
                    case "summary" -> this.summary = book.summary();
                }
            }
        }
    }
}

