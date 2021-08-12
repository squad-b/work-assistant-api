package com.squadb.workassistantapi.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Isbn {
    // https://howtodoinjava.com/java/regex/java-regex-validate-international-standard-book-number-isbns/ 참고함
    private static final Pattern isbnPattern = Pattern.compile(
            "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})" +
                    "[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})" +
                    "[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$");

    @Getter
    @Column(unique = true, nullable = false, length = 13, name = "isbn")
    private String value;

    private Isbn(String value) {
        validateNotNull(value);
        validateIsbnPattern(value);
        this.value = value;
    }

    private void validateNotNull(String value) {
        if (value == null) {
            throw new IllegalArgumentException("isbn 의 값이 없습니다.");
        }
    }

    private void validateIsbnPattern(String value) {
        Matcher matcher = isbnPattern.matcher(value);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("isbn 형식이 아닙니다. [" + value + "]");
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
