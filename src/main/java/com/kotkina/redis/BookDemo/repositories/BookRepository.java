package com.kotkina.redis.BookDemo.repositories;

import com.kotkina.redis.BookDemo.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Override
    @EntityGraph("book-with-category")
    Optional<Book> findById(Long aLong);

    @EntityGraph("book-with-category")
    Optional<Book> findFirstByTitleAndAuthor(String title, String author);

    @EntityGraph("book-with-category")
    Page<Book> findBooksByCategoryName(String name, Pageable pageable);
}
