package com.linkup.growth.invite.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.linkup.common.enums.DatabaseEnum;

/**
 * 邀请奖励状态。
 *
 * <p>对应 PostgreSQL enum：invite_status。
 * pending 表示好友已注册但未完成首单；
 * rewarded 表示好友完成首单，双方已得奖励。</p>
 */
public enum InviteStatus implements DatabaseEnum {

    PENDING("pending"),
    REWARDED("rewarded");

    private final String value;

    InviteStatus(String value) {
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
