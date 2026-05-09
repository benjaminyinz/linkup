package com.linkup.commerce.order.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.linkup.common.enums.DatabaseEnum;

/**
 * 退款状态。
 *
 * <p>对应 PostgreSQL enum：refund_status。</p>
 */
public enum RefundStatus implements DatabaseEnum {

    NONE("none"),
    REQUESTED("requested"),
    PROCESSING("processing"),
    COMPLETED("completed");

    private final String value;

    RefundStatus(String value) {
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
