package com.kotkina.redis.BookDemo.web.models.responses;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponseList implements Serializable {

    private int page;
    private int size;
    private int totalPages;
    @Builder.Default
    private List<BookResponse> books = new ArrayList<>();
}
