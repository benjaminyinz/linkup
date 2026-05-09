package com.linkup.finance.wallet.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.linkup.common.enums.DatabaseEnum;

/**
 * 钱包流水关联业务类型。
 *
 * <p>对应 PostgreSQL enum：wallet_ref_type。</p>
 */
public enum WalletRefType implements DatabaseEnum {

    ACTIVITY_PARTICIPANT("activity_participant"),
    APP_ORDER("app_order");

    private final String value;

    WalletRefType(String value) {
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
