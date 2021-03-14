package com.squadb.workassistantapi.web.controller.dto;

import com.squadb.workassistantapi.domain.Book;
import com.squadb.workassistantapi.domain.BookCategory;
import com.squadb.workassistantapi.web.exception.InvalidRequestBodyException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
public class BookRegisterRequestDto {

    private String isbn;
    private String title;
    private String description;
    private String author;
    private LocalDateTime publishingDate;
    private String publisher;
    private BookCategory category;
    private String imageUrl;
    private int stockQuantity;

    public Book toEntity() {
        if (!isValid()) { throw new InvalidRequestBodyException(String.format("Invalid Parameters! [%s]", toString())); }
        return Book.builder()
                .isbn(isbn)
                .title(title)
                .description(description)
                .author(author)
                .stockQuantity(stockQuantity)
                .imageUrl(imageUrl)
                .publishingDate(publishingDate)
                .publisher(publisher)
                .category(category)
                .build();
    }

    private boolean isValid() {
        final long isbn13Length = 13;
        final long isbn10Length = 10;
        return StringUtils.hasText(isbn) && (isbn.length() == isbn13Length || isbn.length() == isbn10Length)
                && StringUtils.hasText(title)
                && stockQuantity > 0;
    }
}
