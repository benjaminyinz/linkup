package com.linkup.activity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.linkup.common.enums.DatabaseEnum;

/**
 * 活动打款状态。
 *
 * <p>对应 PostgreSQL enum：payout_status。</p>
 */
public enum PayoutStatus implements DatabaseEnum {

    PENDING("pending"),
    PROCESSING("processing"),
    COMPLETED("completed");

    private final String value;

    PayoutStatus(String value) {
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
