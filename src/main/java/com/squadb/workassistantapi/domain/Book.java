package com.squadb.workassistantapi.domain;

import com.squadb.workassistantapi.domain.exceptions.NoAuthorizationException;
import com.squadb.workassistantapi.domain.exceptions.OutOfStockException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Isbn isbn;

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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "registrant_id", nullable = false)
    private Member registrant;

    @Version
    private Long version;

    public void removeStock() {
        if (isOutOfStock()) { throw new OutOfStockException(String.format("Out of stock, Id:[%d]", id)); }
        stockQuantity -= 1;
    }

    public void increaseStock() {
        stockQuantity++;
    }

    public boolean isOutOfStock() {
        return stockQuantity <= 0;
    }

    @Builder
    public Book(Isbn isbn, String title, String description, String author, int stockQuantity, String imageUrl,
                LocalDateTime publishingDate, String publisher, BookCategory category, Member registrant) {
        if (!registrant.isAdmin()) { throw new NoAuthorizationException("관리자만 책을 등록할 수 있습니다."); }
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
        this.registrant = registrant;
    }
}
