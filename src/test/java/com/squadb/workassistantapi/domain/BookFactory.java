package com.squadb.workassistantapi.domain;

import java.time.LocalDateTime;

import static com.squadb.workassistantapi.domain.IsbnTest.isbn;

public class BookFactory {

    public static Book createBookRegisteredBy(Member member) {
        int stockQuantity = 1;
        return createBook(member, stockQuantity);
    }

    public static Book createBookOutOfStockRegisteredBy(Member member) {
        int stockQuantity = 0;
        return createBook(member, stockQuantity);
    }

    private static Book createBook(Member member, int stockQuantity) {
        return Book.builder()
                .isbn(isbn)
                .title("Spring")
                .stockQuantity(StockQuantity.valueOf(stockQuantity))
                .registrationDate(LocalDateTime.now())
                .registrant(member)
                .build();
    }
}
