package com.linkup.growth.voucher.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.linkup.common.enums.DatabaseEnum;

/**
 * 优惠券来源。
 *
 * <p>对应 PostgreSQL enum：voucher_source。</p>
 */
public enum VoucherSource implements DatabaseEnum {

    /** 邀请好友奖励 */
    INVITE_REWARD("invite_reward"),

    /** 活动相关赠券 */
    ACTIVITY("activity"),

    /** 平台发放 */
    PLATFORM("platform");

    private final String value;

    VoucherSource(String value) {
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
