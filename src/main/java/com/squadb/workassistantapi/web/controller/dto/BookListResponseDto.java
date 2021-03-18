package com.squadb.workassistantapi.web.controller.dto;

import com.squadb.workassistantapi.domain.Book;
import com.squadb.workassistantapi.domain.BookCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookListResponseDto {

    private long id;
    private String title;
    private String imageUrl;
    private BookCategory category;
    private long stockQuantity;

    private static BookListResponseDto of(Book book) {
        BookListResponseDto bookListResponseDto = new BookListResponseDto();
        bookListResponseDto.id = book.getId();
        bookListResponseDto.title = book.getTitle();
        bookListResponseDto.imageUrl = book.getImageUrl();
        bookListResponseDto.category = book.getCategory();
        bookListResponseDto.stockQuantity = book.getStockQuantity();
        return bookListResponseDto;
    }

    public static List<BookListResponseDto> of(List<Book> bookList) {
        return bookList.stream().map(book -> of(book)).collect(Collectors.toList());
    }
}
