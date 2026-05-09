package com.linkup.commerce.deal.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.linkup.common.enums.DatabaseEnum;

/**
 * 团购品类。
 *
 * <p>对应 PostgreSQL enum：deal_segment。</p>
 */
public enum DealSegment implements DatabaseEnum {

    FOOD("food"),
    ENTERTAINMENT("entertainment"),
    SPORTS("sports"),
    BEAUTY("beauty");

    private final String value;

    DealSegment(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
