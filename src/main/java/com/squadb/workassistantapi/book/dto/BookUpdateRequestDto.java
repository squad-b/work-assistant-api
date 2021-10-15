package com.squadb.workassistantapi.book.dto;

import com.squadb.workassistantapi.book.domain.BookCategory;
import lombok.Data;

@Data
public class BookUpdateRequestDto {
    private BookCategory bookCategory;
    private Integer stockQuantity;
}
