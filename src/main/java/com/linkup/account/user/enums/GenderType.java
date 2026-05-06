package com.linkup.account.user.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.linkup.common.enums.DatabaseEnum;

/**
 * 用户性别。
 *
 * <p>对应 PostgreSQL enum：gender_type。</p>
 */
public enum GenderType implements DatabaseEnum {

    /**
     * 男。
     */
    MALE("male"),

    /**
     * 女。
     */
    FEMALE("female");

    private final String value;

    GenderType(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String getValue() {
        return value;
    }

    public static GenderType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (GenderType genderType : values()) {
            if (genderType.value.equals(value)) {
                return genderType;
            }
        }
        throw new IllegalArgumentException("未知的性别值：" + value);
    }

    @Override
    public String toString() {
        return value;
    }
}
