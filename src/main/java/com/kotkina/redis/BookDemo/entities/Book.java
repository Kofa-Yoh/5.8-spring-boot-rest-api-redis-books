package com.kotkina.redis.BookDemo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@NamedEntityGraph(name = "book-with-category",
        attributeNodes = @NamedAttributeNode("category")
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Book implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String author;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    public void changeCategoryName(String name) {
        if (category != null) {
            category.setName(name);
        }
    }

    public static Book create(String title, String author) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);

        return book;
    }
}
