package com.kotkina.redis.BookDemo.web.controllers;

import com.kotkina.redis.BookDemo.services.BookService;
import com.kotkina.redis.BookDemo.web.models.requests.BookRequest;
import com.kotkina.redis.BookDemo.web.models.responses.BookResponse;
import com.kotkina.redis.BookDemo.web.models.responses.BookResponseList;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@Validated
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final static int DEFAULT_PAGE_NUMBER = 0;
    private final static int DEFAULT_PAGE_SIZE = 3;

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody @Valid BookRequest bookRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(bookRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id,
                                                   @RequestBody @Valid BookRequest bookRequest) {
        return ResponseEntity.ok(bookService.update(id, bookRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.delete(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<BookResponse> findBooksByTitleAndAuthor(@RequestParam String title,
                                                                  @RequestParam String author) {
        return bookService
                .findBookByTitleAndAuthor(title, author)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/by-category")
    public ResponseEntity<BookResponseList> findBooksByCategoryName(@RequestParam @NotBlank String name,
                                                                    @RequestParam(required = false) @Min(0) Integer page,
                                                                    @RequestParam(required = false) @Min(1) Integer size) {
        if (page == null) {
            page = DEFAULT_PAGE_NUMBER;
        }
        if (size == null) {
            size = DEFAULT_PAGE_SIZE;
        }

        return bookService
                .findBooksByCategoryName(name, page, size)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
