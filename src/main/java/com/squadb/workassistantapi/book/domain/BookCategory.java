package com.squadb.workassistantapi.book.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BookCategory {
    DEVELOP("개발"),
    MANAGEMENT("경영"),
    PLAN("기획"),
    MARKETING("마켓팅"),
    SELF_IMPROVEMENT("자기계발"),
    LICENSE("자격증"),
    DESIGN("디자인"),
    FICTION("소설"),
    NONFICTION("비소설");

    @Getter
    private final String korean;
}
