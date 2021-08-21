package com.squadb.workassistantapi.book.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

/**
 * third party book api 로 넘어온 책 하나의 정보
 */
@Getter
public class BookSearchDocument {

    private List<String> authors;

    private String contents;

    @JsonProperty("datetime")
    private String publishingDate;

    private String publisher;

    @JsonProperty("thumbnail")
    private String imgUrl;

    private String title;

    @JsonProperty("url")
    private String descriptionUrl;

    private String isbn;
}
