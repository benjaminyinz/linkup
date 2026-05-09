package com.linkup.account.auth.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.linkup.common.enums.DatabaseEnum;

/**
 * 登录方式。
 *
 * <p>对应 PostgreSQL enum：auth_provider。
 * 当前只支持邮箱登录，后续接入 Apple / Google 时在此追加新值。</p>
 */
public enum AuthProvider implements DatabaseEnum {

    EMAIL("email");

    private final String value;

    AuthProvider(String value) {
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
