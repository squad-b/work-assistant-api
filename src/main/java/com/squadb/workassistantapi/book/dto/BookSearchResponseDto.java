package com.squadb.workassistantapi.book.dto;

import lombok.Getter;

import java.util.List;

/**
 * third party book api 의 결과
 */
@Getter
public class BookSearchResponseDto {
    private List<BookSearchDocument> documents;
    private BookSearchMeta meta;
}
