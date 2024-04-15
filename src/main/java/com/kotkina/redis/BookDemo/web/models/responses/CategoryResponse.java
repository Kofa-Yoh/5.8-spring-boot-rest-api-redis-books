package com.kotkina.redis.BookDemo.web.models.responses;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse implements Serializable {

    private Long id;
    private String name;
}
