package com.linkup.commerce.order.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.linkup.common.enums.DatabaseEnum;

/**
 * 团购订单核销状态。
 *
 * <p>对应 PostgreSQL enum：redeem_status。</p>
 */
public enum RedeemStatus implements DatabaseEnum {

    UNUSED("unused"),
    USED("used"),
    EXPIRED("expired");

    private final String value;

    RedeemStatus(String value) {
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
