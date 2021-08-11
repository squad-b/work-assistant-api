package com.squadb.workassistantapi.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockQuantity {

    @Column(nullable = false, name = "stock_quantity")
    private int value;

    private StockQuantity(int value) {
        validateNonNegative(value);
        this.value = value;
    }

    private void validateNonNegative(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("재고 수는 0 이상이어야 합니다.");
        }
    }

    public static StockQuantity valueOf(int value) {
        return new StockQuantity(value);
    }

    public StockQuantity minus(StockQuantity other) {
        return StockQuantity.valueOf(this.value - other.value);
    }

    public StockQuantity plus(StockQuantity other) {
        return StockQuantity.valueOf(this.value + other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockQuantity that = (StockQuantity) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
