package com.linkup.finance.wallet.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.linkup.common.enums.DatabaseEnum;

/**
 * 钱包流水状态。
 *
 * <p>对应 PostgreSQL enum：wallet_tx_status。</p>
 */
public enum WalletTxStatus implements DatabaseEnum {

    PENDING("pending"),
    COMPLETED("completed"),
    FAILED("failed");

    private final String value;

    WalletTxStatus(String value) {
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
