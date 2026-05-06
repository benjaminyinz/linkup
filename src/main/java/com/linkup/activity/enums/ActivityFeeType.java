package com.linkup.activity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.linkup.common.enums.DatabaseEnum;

/**
 * 活动收费类型。
 *
 * <p>对应 PostgreSQL enum：activity_fee_type。</p>
 */
public enum ActivityFeeType implements DatabaseEnum {

    /**
     * 免费活动。
     */
    FREE("free"),

    /**
     * AA 制，到场各自付款。
     */
    SPLIT("split"),

    /**
     * 固定收费，由 host 定价，平台代收。
     */
    FIXED("fixed");

    private final String value;

    ActivityFeeType(String value) {
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
