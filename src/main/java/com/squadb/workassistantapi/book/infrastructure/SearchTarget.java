package com.squadb.workassistantapi.book.infrastructure;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SearchTarget {
    TITLE("title"),
    ISBN("isbn");

    @Getter
    private final String field;
}
