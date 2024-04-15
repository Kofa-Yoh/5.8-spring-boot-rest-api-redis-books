package com.kotkina.redis.BookDemo.services;

import com.kotkina.redis.BookDemo.config.annotations.CacheEvictByPrefix;
import com.kotkina.redis.BookDemo.entities.Book;
import com.kotkina.redis.BookDemo.entities.Category;
import com.kotkina.redis.BookDemo.repositories.BookRepository;
import com.kotkina.redis.BookDemo.repositories.CategoryRepository;
import com.kotkina.redis.BookDemo.web.models.requests.BookRequest;
import com.kotkina.redis.BookDemo.web.models.responses.BookResponse;
import com.kotkina.redis.BookDemo.web.models.responses.BookResponseList;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static com.kotkina.redis.BookDemo.config.properties.AppCacheProperties.CacheNames.DATABASE_BOOKS_BY_CATEGORY_NAME;
import static com.kotkina.redis.BookDemo.config.properties.AppCacheProperties.CacheNames.DATABASE_BOOKS_BY_TITLE_AND_AUTHOR;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheManager = "redisCacheManager", keyGenerator = "bookKeyGenerator")
public class BookService {

    private static final String CASH_KEY_DELIMITER = "_";
    private final BookRepository bookRepository;

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    public static final Type BOOK_RESPONSE_LIST_MAPPING_TYPE = new TypeToken<List<BookResponse>>() {
    }.getType();

    public static final String BOOK_ENTITY_NOT_FOUND_MESSAGE = "Книга с таким id не найдена.";

    @CacheEvict(cacheNames = DATABASE_BOOKS_BY_TITLE_AND_AUTHOR, key = "#bookRequest.title + '" + CASH_KEY_DELIMITER + "' + #bookRequest.author", beforeInvocation = true)
    @CacheEvictByPrefix(cacheName = DATABASE_BOOKS_BY_CATEGORY_NAME, key = "#bookRequest.category", prefixEnd = CASH_KEY_DELIMITER)
    @Transactional
    public BookResponse create(BookRequest bookRequest) {
        Category savedCategory = categoryRepository.save(Category.create(bookRequest.getCategory()));

        Book newBook = Book.create(bookRequest.getTitle(), bookRequest.getAuthor());
        newBook.setCategory(savedCategory);

        return modelMapper.map(bookRepository.save(newBook), BookResponse.class);
    }

    @CacheEvict(cacheNames = DATABASE_BOOKS_BY_TITLE_AND_AUTHOR, key = "#bookRequest.title + '" + CASH_KEY_DELIMITER + "' + #bookRequest.author", beforeInvocation = true)
    @CacheEvictByPrefix(cacheName = DATABASE_BOOKS_BY_CATEGORY_NAME, key = "#bookRequest.category", prefixEnd = CASH_KEY_DELIMITER)
    @Transactional
    public BookResponse update(Long id, BookRequest bookRequest) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(BOOK_ENTITY_NOT_FOUND_MESSAGE));

        book.setTitle(bookRequest.getTitle());
        book.setAuthor(bookRequest.getAuthor());
        book.changeCategoryName(bookRequest.getCategory());

        return modelMapper.map(bookRepository.save(book), BookResponse.class);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = DATABASE_BOOKS_BY_TITLE_AND_AUTHOR, allEntries = true, beforeInvocation = true),
            @CacheEvict(cacheNames = DATABASE_BOOKS_BY_CATEGORY_NAME, allEntries = true, beforeInvocation = true)
    })
    @Transactional
    public void delete(Long id) {
        bookRepository.findById(id).ifPresentOrElse(
                b -> bookRepository.deleteById(id),
                () -> {
                    throw new EntityNotFoundException(BOOK_ENTITY_NOT_FOUND_MESSAGE);
                });
    }

    @Cacheable(cacheNames = DATABASE_BOOKS_BY_TITLE_AND_AUTHOR)
    public Optional<BookResponse> findBookByTitleAndAuthor(String title, String author) {
        return bookRepository.findFirstByTitleAndAuthor(title, author)
                .map(book -> modelMapper.map(book, BookResponse.class));
    }

    @Cacheable(cacheNames = DATABASE_BOOKS_BY_CATEGORY_NAME)
    public Optional<BookResponseList> findBooksByCategoryName(String name, Integer page, Integer size) {
        return bookListPageToBookResponseList(
                bookRepository.findBooksByCategoryName(name, PageRequest.of(page, size)));
    }

    private Optional<BookResponseList> bookListPageToBookResponseList(Page<Book> books) {
        if (books == null || books.getTotalElements() == 0) return Optional.empty();

        return Optional.of(BookResponseList.builder()
                .books(modelMapper.map(books.getContent(), BOOK_RESPONSE_LIST_MAPPING_TYPE))
                .page(books.getNumber())
                .size(books.getNumberOfElements())
                .totalPages(books.getTotalPages())
                .build());
    }
}
