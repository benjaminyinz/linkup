package com.linkup.account.auth.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linkup.account.auth.enums.AuthProvider;
import com.linkup.infrastructure.typehandler.PostgreSQLEnumTypeHandler;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

/**
 * 用户登录凭证。
 *
 * <p>对应数据库表 user_auth。
 * 当前 MVP 仅支持邮箱登录，provider_user_id 存邮箱地址，password_hash 存 bcrypt 哈希。</p>
 */
@Data
@TableName(value = "user_auth", autoResultMap = true)
public class UserAuth implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID userId;

    @TableField(value = "provider", typeHandler = PostgreSQLEnumTypeHandler.class)
    private AuthProvider provider;

    private String providerUserId;

    private String passwordHash;

    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;

    private static final long serialVersionUID = 1L;
}
