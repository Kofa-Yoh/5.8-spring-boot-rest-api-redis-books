package com.kotkina.redis.BookDemo.web.models.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequest {

    @NotBlank
    private String title;
    @NotBlank
    private String author;
    @NotBlank
    private String category;
}
