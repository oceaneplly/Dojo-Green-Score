package com.greenapi.optimized.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateRequest {
    private String title;
    private String author;
    private Integer published_date;
    private Integer pages;
    private String summary;
}
