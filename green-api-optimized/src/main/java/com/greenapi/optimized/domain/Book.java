package com.greenapi.optimized.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private long id;
    private String title;
    private String author;
    private int published_date;
    private int pages;
    private String summary;
    private long lastModified;

    public Book(long id, String title, String author, int published_date, int pages, String summary) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.published_date = published_date;
        this.pages = pages;
        this.summary = summary;
        this.lastModified = System.currentTimeMillis();
    }

    private void updateLastModified() {
        this.lastModified = System.currentTimeMillis();
    }

    public void setTitle(String title) {
        this.title = title;
        updateLastModified();
    }

    public void setAuthor(String author) {
        this.author = author;
        updateLastModified();
    }

    public void setPublished_date(int published_date) {
        this.published_date = published_date;
        updateLastModified();
    }

    public void setPages(int pages) {
        this.pages = pages;
        updateLastModified();
    }

    public void setSummary(String summary) {
        this.summary = summary;
        updateLastModified();
    }
}
