package com.linkup.growth.voucher.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.linkup.common.enums.DatabaseEnum;

/**
 * 优惠券状态。
 *
 * <p>对应 PostgreSQL enum：voucher_status。</p>
 */
public enum VoucherStatus implements DatabaseEnum {

    UNUSED("unused"),
    USED("used"),
    EXPIRED("expired");

    private final String value;

    VoucherStatus(String value) {
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
