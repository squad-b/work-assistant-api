package com.squadb.workassistantapi.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockQuantityTest {

    @DisplayName("재고 수는 0 이상이어야 한다.")
    @Test
    void positiveStockQuantityTest() {
        assertThatThrownBy(() -> StockQuantity.valueOf(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고 수는 0 이상이어야 합니다.");
    }

    @DisplayName("재고 수끼리 뺄셈을 할 수 있다.")
    @Test
    void stockQuantityMinusTest() {
        StockQuantity four = StockQuantity.valueOf(4);
        StockQuantity one = StockQuantity.valueOf(1);

        assertThat(four.minus(one)).isEqualTo(StockQuantity.valueOf(3));
    }

    @DisplayName("재고 수끼리 덧셈을 할 수 있다.")
    @Test
    void stockQuantityPlusTest() {
        StockQuantity four = StockQuantity.valueOf(4);
        StockQuantity one = StockQuantity.valueOf(1);

        assertThat(four.plus(one)).isEqualTo(StockQuantity.valueOf(5));
    }

    @DisplayName("재고 수끼리 수량 비교를 할 수 있다.")
    @Test
    void comparableStockQuantityTest() {
        StockQuantity four = StockQuantity.valueOf(4);
        StockQuantity one = StockQuantity.valueOf(1);

        assertThat(four.compareTo(one)).isEqualTo(1);
        assertThat(one.compareTo(four)).isEqualTo(-1);
        assertThat(one.compareTo(StockQuantity.valueOf(1))).isEqualTo(0);
    }
}