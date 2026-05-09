package com.linkup.activity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.linkup.common.enums.DatabaseEnum;

/**
 * 活动参与状态。
 *
 * <p>对应 PostgreSQL enum：participant_status。
 * 不支持候补，满员后直接拒绝加入。</p>
 */
public enum ParticipantStatus implements DatabaseEnum {

    JOINED("joined"),
    CANCELLED("cancelled"),
    COMPLETED("completed");

    private final String value;

    ParticipantStatus(String value) {
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
