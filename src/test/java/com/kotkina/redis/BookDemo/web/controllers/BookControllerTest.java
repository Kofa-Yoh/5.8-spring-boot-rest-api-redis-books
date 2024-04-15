package com.kotkina.redis.BookDemo.web.controllers;

import com.kotkina.redis.BookDemo.AbstractTest;
import com.kotkina.redis.BookDemo.web.models.requests.BookRequest;
import com.kotkina.redis.BookDemo.web.models.responses.BookResponse;
import com.kotkina.redis.BookDemo.web.models.responses.CategoryResponse;
import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.kotkina.redis.BookDemo.config.properties.AppCacheProperties.CacheNames.DATABASE_BOOKS_BY_CATEGORY_NAME;
import static com.kotkina.redis.BookDemo.config.properties.AppCacheProperties.CacheNames.DATABASE_BOOKS_BY_TITLE_AND_AUTHOR;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookControllerTest extends AbstractTest {

    @Test
    public void whenGetBooksByTitleAndAuthor_thenReturnBookAndSaveCache() throws Exception {
        String title = "1984", author = "George Orwell";
        String keyPattern = DATABASE_BOOKS_BY_TITLE_AND_AUTHOR + "::" + String.join("_", title, author) + "*";

        assertTrue(redisTemplate.keys(keyPattern).isEmpty());

        String actualResponse = mockMvc.perform(get("/api/books")
                        .param("title", title)
                        .param("author", author))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = objectMapper.writeValueAsString(
                bookService.findBookByTitleAndAuthor(title, author).get());

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
        assertFalse(redisTemplate.keys(keyPattern).isEmpty());
    }

    @Test
    public void whenGetBooksByCategory_thenReturnBooksListAndSaveCache() throws Exception {
        String category = "fiction";

        assertTrue(redisTemplate.keys("*").isEmpty());

        String actualResponse = mockMvc.perform(get("/api/books/by-category")
                        .param("name", category))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = objectMapper.writeValueAsString(
                bookService.findBooksByCategoryName(category, 0, 3).get());

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
        assertFalse(redisTemplate.keys("*").isEmpty());
    }

    @Test
    public void whenCreateBook_thenCreateBookAndEvictCache() throws Exception {
        String title = "New Title", author = "Author", category = "fiction";
        BookRequest request = new BookRequest(title, author, category);
        BookResponse response = new BookResponse(1L, title, author, new CategoryResponse(1L, category));
        String titleAuthorKeyPattern = DATABASE_BOOKS_BY_TITLE_AND_AUTHOR + "::" + String.join("_", title, author) + "*";
        String categoryKeyPattern = DATABASE_BOOKS_BY_CATEGORY_NAME + "::" + category + "_*";

        assertTrue(redisTemplate.keys("*").isEmpty());
        long booksCount = bookRepository.count();
        long categoriesCount = categoryRepository.count();

        mockMvc.perform(get("/api/books/by-category")
                        .param("name", category))
                .andReturn();

        mockMvc.perform(get("/api/books")
                        .param("title", title)
                        .param("author", author))
                .andReturn();

        assertFalse(redisTemplate.keys(titleAuthorKeyPattern).isEmpty());
        assertFalse(redisTemplate.keys(categoryKeyPattern).isEmpty());
        long titleAuthorCacheBeforePost = redisTemplate.keys(titleAuthorKeyPattern).size();
        long categoryCacheBeforePost = redisTemplate.keys(categoryKeyPattern).size();

        String actualResponse = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectResponse = objectMapper.writeValueAsString(response);

        JsonAssert.assertJsonEquals(expectResponse, actualResponse, JsonAssert.whenIgnoringPaths("id", "category.id"));
        assertEquals(booksCount + 1, bookRepository.count());
        assertEquals(categoriesCount + 1, categoryRepository.count());
        assertEquals(titleAuthorCacheBeforePost - 1, redisTemplate.keys(titleAuthorKeyPattern).size());
        assertEquals(categoryCacheBeforePost - 1, redisTemplate.keys(categoryKeyPattern).size());
    }

    @Test
    public void whenUpdateBook_thenUpdateBookAndEvictCache() throws Exception {
        Long id = 1L;
        String title = "New Title", author = "Author", category = "fiction";
        BookRequest request = new BookRequest(title, author, category);
        BookResponse response = new BookResponse(id, title, author, new CategoryResponse(1L, category));
        String titleAuthorKeyPattern = DATABASE_BOOKS_BY_TITLE_AND_AUTHOR + "::" + String.join("_", title, author) + "*";
        String categoryKeyPattern = DATABASE_BOOKS_BY_CATEGORY_NAME + "::" + category + "_*";

        assertTrue(redisTemplate.keys("*").isEmpty());

        mockMvc.perform(get("/api/books/by-category")
                        .param("name", category))
                .andReturn();

        mockMvc.perform(get("/api/books")
                        .param("title", title)
                        .param("author", author))
                .andReturn();

        assertFalse(redisTemplate.keys(titleAuthorKeyPattern).isEmpty());
        assertFalse(redisTemplate.keys(categoryKeyPattern).isEmpty());
        long titleAuthorCacheBeforePost = redisTemplate.keys(titleAuthorKeyPattern).size();
        long categoryCacheBeforePost = redisTemplate.keys(categoryKeyPattern).size();

        String actualResponse = mockMvc.perform(put("/api/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectResponse = objectMapper.writeValueAsString(response);

        JsonAssert.assertJsonEquals(expectResponse, actualResponse, JsonAssert.whenIgnoringPaths("category.id"));
        assertEquals(titleAuthorCacheBeforePost - 1, redisTemplate.keys(titleAuthorKeyPattern).size());
        assertEquals(categoryCacheBeforePost - 1, redisTemplate.keys(categoryKeyPattern).size());
    }

    @Test
    public void whenDeleteBook_thenDeleteBookAndEvictCache() throws Exception {
        Long id = 1L;

        assertTrue(redisTemplate.keys("*").isEmpty());

        mockMvc.perform(get("/api/books/by-category")
                        .param("name", "fiction"))
                .andReturn();

        mockMvc.perform(get("/api/books")
                        .param("title", "1984")
                        .param("author", "George Orwell"))
                .andReturn();

        assertFalse(redisTemplate.keys("*").isEmpty());

        mockMvc.perform(delete("/api/books/" + id))
                .andExpect(status().isOk());

        assertTrue(bookRepository.findById(id).isEmpty());
        assertTrue(redisTemplate.keys("*").isEmpty());
    }
}