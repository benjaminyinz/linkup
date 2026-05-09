package com.linkup.commerce.deal.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.linkup.common.enums.DatabaseEnum;

/**
 * 团购套餐状态。
 *
 * <p>对应 PostgreSQL enum：deal_status。
 * ended 同时表示自然到期和手动下架。</p>
 */
public enum DealStatus implements DatabaseEnum {

    ACTIVE("active"),
    ENDED("ended");

    private final String value;

    DealStatus(String value) {
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
