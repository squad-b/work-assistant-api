package com.squadb.workassistantapi.book.domain;

import com.squadb.workassistantapi.member.domain.Member;
import com.squadb.workassistantapi.rental.domain.NoAuthorizationException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Embedded
    private Isbn isbn;

    @Getter
    @Column(nullable = false)
    private String title;

    @Getter
    @Lob
    @Column
    private String description;

    @Getter
    @Column
    private String author;

    @Getter
    @Embedded
    private StockQuantity stockQuantity;

    @Getter
    @Column
    private String imageUrl;

    @Getter
    @Column
    private LocalDateTime publishingDate;

    @Getter
    @Column(nullable = false)
    private LocalDateTime registrationDate;

    @Getter
    @Column
    private String publisher;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column
    private BookCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrant_id", nullable = false)
    private Member registrant;

    @Version
    private Long version;

    public void decreaseStock() {
        if (isOutOfStock()) { throw new OutOfStockException(String.format("Out of stock, Id:[%d]", id)); }
        stockQuantity = stockQuantity.minusOne();
    }

    public void increaseStock() {
        stockQuantity = stockQuantity.plusOne();
    }

    public boolean isOutOfStock() {
        return stockQuantity.isZero();
    }

    @Builder
    public Book(Isbn isbn, String title, String description, String author, StockQuantity stockQuantity, String imageUrl,
                LocalDateTime publishingDate, String publisher, BookCategory category, Member registrant,
                LocalDateTime registrationDate) {
        validateNotNull(isbn, title, stockQuantity, registrant, registrationDate);
        validateAdmin(registrant);
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.author = author;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.publishingDate = publishingDate;
        this.registrationDate = registrationDate;
        this.publisher = publisher;
        this.category = category;
        this.registrant = registrant;
    }

    public void update(BookCategory category, StockQuantity stockQuantity, Member updater) {
        validateAdmin(updater);
        validateNotNull(stockQuantity);
        this.category = category;
        this.stockQuantity = stockQuantity;
    }

    private void validateAdmin(Member member) {
        if (!member.isAdmin()) { throw new NoAuthorizationException("관리자만 책을 등록, 수정할 수 있습니다."); }
    }

    private void validateNotNull(StockQuantity stockQuantity) {
        if (stockQuantity == null) {
            throw new IllegalArgumentException("책 수량이 존재하지 않습니다.");
        }
    }

    private void validateNotNull(Isbn isbn, String title, StockQuantity stockQuantity,
                                 Member registrant, LocalDateTime registrationDate) {
        if (isNull(isbn) || isNull(title) || isNull(stockQuantity) || isNull(registrant) || isNull(registrationDate)) {
            throw new IllegalArgumentException("책 필수 정보가 없습니다.");
        }
    }

    public String getIsbnValue() {
        return isbn.getValue();
    }

    public int getStockQuantityValue() {
        return stockQuantity.getValue();
    }

    public String getCategoryKoreanName() {
        return category.getKorean();
    }
}
