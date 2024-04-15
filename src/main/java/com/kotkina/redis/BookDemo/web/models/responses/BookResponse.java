package com.kotkina.redis.BookDemo.web.models.responses;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponse implements Serializable {

    private Long id;
    private String title;
    private String author;
    private CategoryResponse category;
}
