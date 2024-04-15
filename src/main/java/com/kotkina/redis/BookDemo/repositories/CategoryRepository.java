package com.kotkina.redis.BookDemo.repositories;

import com.kotkina.redis.BookDemo.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
