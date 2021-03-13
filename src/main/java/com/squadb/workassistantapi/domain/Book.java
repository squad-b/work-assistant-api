package com.squadb.workassistantapi.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false, length = 13)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column
    private String description;

    @Column
    private String author;

    @Column(nullable = false)
    public int stockQuantity;

    @Column
    private String imageUrl;

    @Column
    private LocalDateTime publishingDate;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    @Column
    private String publisher;

    @Enumerated(EnumType.STRING)
    @Column
    private BookCategory category;

    public void removeStock() {
        if (isOutOfStock()) { throw new IllegalStateException(String.format("Out of stock, Id:[%d]", id)); }
        stockQuantity -= 1;
    }

    public boolean isOutOfStock() {
        return stockQuantity <= 0;
    }

    @Builder
    public Book(String isbn, String title, String description, String author, int stockQuantity, String imageUrl, LocalDateTime publishingDate, String publisher, BookCategory category) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.author = author;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.publishingDate = publishingDate;
        this.registrationDate = LocalDateTime.now();
        this.publisher = publisher;
        this.category = category;
    }
}
