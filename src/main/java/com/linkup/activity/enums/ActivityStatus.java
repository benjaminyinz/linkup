package com.linkup.activity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.linkup.common.enums.DatabaseEnum;

/**
 * 活动状态。
 *
 * <p>对应 PostgreSQL enum：activity_status。</p>
 */
public enum ActivityStatus implements DatabaseEnum {

    /**
     * 可报名。
     */
    OPEN("open"),

    /**
     * 已满员。
     */
    FULL("full"),

    /**
     * 已完成。
     */
    COMPLETED("completed"),

    /**
     * 已取消。
     */
    CANCELLED("cancelled");

    private final String value;

    ActivityStatus(String value) {
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
