package com.squadb.workassistantapi.web.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.squadb.workassistantapi.domain.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
public class BookRegisterDto {

    private String isbn;
    private String title;
    private String description;
    private String author;

    // 2018-05-10T00:00:00.000+09:00
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Asia/Seoul")
    private LocalDateTime publishingDate;
    private String publisher;
    private BookCategory category;
    private String imageUrl;
    private int stockQuantity;

    public Book toEntity(Member registrant) {
        return Book.builder()
                .isbn(Isbn.valueOf(isbn))
                .title(title)
                .description(description)
                .author(author)
                .stockQuantity(StockQuantity.valueOf(stockQuantity))
                .imageUrl(imageUrl)
                .publishingDate(publishingDate)
                .publisher(publisher)
                .category(category)
                .registrant(registrant)
                .registrationDate(LocalDateTime.now())
                .build();
    }

    public void checkValidation() {
        if (!isValid()) { throw new IllegalArgumentException(String.format("Invalid Parameters! [%s]", this)); }
    }

    private boolean isValid() {
        return StringUtils.hasText(title);
    }
}
