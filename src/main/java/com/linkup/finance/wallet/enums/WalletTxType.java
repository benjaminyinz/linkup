package com.linkup.finance.wallet.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.linkup.common.enums.DatabaseEnum;

/**
 * 钱包流水类型。
 *
 * <p>对应 PostgreSQL enum：wallet_tx_type。</p>
 */
public enum WalletTxType implements DatabaseEnum {

    /** host 收到参与者付款 */
    ACTIVITY_INCOME("activity_income"),

    /** 退款回到用户 */
    REFUND("refund"),

    /** 用户提现到银行 */
    WITHDRAWAL("withdrawal");

    private final String value;

    WalletTxType(String value) {
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
