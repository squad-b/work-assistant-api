package com.squadb.workassistantapi.book.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class IsbnTest {

    public static final Isbn isbn = Isbn.valueOf("9780596520687");

    @DisplayName("isbn 을 만들기 위해서 문자열 값이 필수이다.")
    @Test
    void isbnNullValueTest() {
        assertThatThrownBy(() -> Isbn.valueOf(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("isbn 의 값이 없습니다.");

    }

    @DisplayName("isbn 의 문자열은 Isbn 형식이어야 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"123456789", "12345678901", "123456789012", "12345678901234"})
    void lengthOfIsbnTest(String notValidIsbnValue) {
        assertThatThrownBy(() -> Isbn.valueOf(notValidIsbnValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("isbn 형식이 아닙니다.");

    }

    @DisplayName("Isbn 값이 같으면 같은 Isbn 이다.")
    @Test
    void equalTest() {
        Isbn one = Isbn.valueOf("0512520689");
        Isbn other = Isbn.valueOf("0512520689");

        assertThat(one).isEqualTo(other);
    }

}
