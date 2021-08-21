package com.squadb.workassistantapi.book.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.squadb.workassistantapi.book.domain.Book;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@JsonInclude(Include.NON_NULL)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookDetailResponseDto {

    private long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String imageUrl;
    private int stockQuantity;
    private String description;
    private String publishingDate;
    private String registrationDate;

    public static BookDetailResponseDto of(Book book) {
        BookDetailResponseDto bookDetailResponseDto = new BookDetailResponseDto();
        bookDetailResponseDto.id = book.getId();
        bookDetailResponseDto.isbn = book.getIsbnValue();
        bookDetailResponseDto.title = book.getTitle();
        bookDetailResponseDto.author = book.getAuthor();
        bookDetailResponseDto.publisher = book.getPublisher();
        bookDetailResponseDto.imageUrl = book.getImageUrl();
        bookDetailResponseDto.stockQuantity = book.getStockQuantityValue();
        bookDetailResponseDto.description = book.getDescription();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        bookDetailResponseDto.publishingDate = book.getPublishingDate().format(formatter);
        bookDetailResponseDto.registrationDate = book.getRegistrationDate().format(formatter);
        return bookDetailResponseDto;
    }
}
