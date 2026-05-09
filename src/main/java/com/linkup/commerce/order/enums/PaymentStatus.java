package com.linkup.commerce.order.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.linkup.common.enums.DatabaseEnum;

/**
 * 支付状态。
 *
 * <p>对应 PostgreSQL enum：payment_status。
 * 同时被 app_order 和 activity_participant 引用。</p>
 */
public enum PaymentStatus implements DatabaseEnum {

    PENDING("pending"),
    PAID("paid"),
    REFUNDED("refunded"),
    FAILED("failed");

    private final String value;

    PaymentStatus(String value) {
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
