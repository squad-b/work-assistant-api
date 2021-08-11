package com.squadb.workassistantapi.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Isbn {

    @Column(unique = true, nullable = false, length = 13)
    private String value;

    private Isbn(String value) {
        validateNotNull(value);
        validateIsbnLength(value);
        this.value = value;
    }

    private void validateNotNull(String value) {
        if (value == null) {
            throw new IllegalArgumentException("isbn 의 값이 없습니다.");
        }
    }

    private void validateIsbnLength(String value) {
        if (value.length() != 10 && value.length() != 13) {
            throw new IllegalArgumentException("isbn 의 길이는 10 or 13 이어야합니다.");
        }
    }

    public static Isbn valueOf(String isbnValue) {
        return new Isbn(isbnValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Isbn isbn = (Isbn) o;
        return Objects.equals(value, isbn.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
