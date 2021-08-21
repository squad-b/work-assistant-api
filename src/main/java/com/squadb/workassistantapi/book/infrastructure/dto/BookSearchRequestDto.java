package com.squadb.workassistantapi.book.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * api 문서
 * https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-book
 */
@Getter
@Setter
public class BookSearchRequestDto {
    // 검색 질의어
    private String query = "";

    // 결과 문서 정렬 방식 (accuracy, latest)
    private String sort = "accuracy";

    // 결과 페이지 번호, 1~50
    private int page = 1;

    // 한 문저세 보여질 문서 1 ~ 50
    private int size = 10;

    // 검색 필드 제한 (isbn, publisher, person)
    private String target = "";

    public String makeUrl(String baseUrl) {
        return String.format("%s?query=%s&page=%s&size=%s&target=%s",
                baseUrl,
                query,
                page,
                size,
                target);
    }
}

