package com.squadb.workassistantapi.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockQuantity implements Comparable<StockQuantity> {
    static StockQuantity ONE = StockQuantity.valueOf(1);

    private static final int MIN_VALUE_OF_STOCK_QUANTITY = 0;

    @Getter
    @Column(nullable = false, name = "stock_quantity")
    private int value;

    private StockQuantity(int value) {
        validateNonNegative(value);
        this.value = value;
    }

    private void validateNonNegative(int value) {
        if (value < MIN_VALUE_OF_STOCK_QUANTITY) {
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

    boolean isZero() {
        return this.value == MIN_VALUE_OF_STOCK_QUANTITY;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        StockQuantity that = (StockQuantity) other;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public int compareTo(StockQuantity other) {
        return Integer.compare(this.value, other.value);
    }
}
