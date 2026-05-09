package com.linkup.account.user.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linkup.account.user.enums.GenderType;
import com.linkup.infrastructure.typehandler.PostgreSQLEnumTypeHandler;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

/**
 * 用户主表。
 *
 * <p>对应数据库表 app_user，避开 PostgreSQL 保留字 user。</p>
 */
@Data
@TableName(value = "app_user", autoResultMap = true)
public class AppUser implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private String nickname;

    private String avatarUrl;

    private String phone;

    private String email;

    private String signature;

    @TableField(value = "gender", typeHandler = PostgreSQLEnumTypeHandler.class)
    private GenderType gender;

    private Integer level;

    private Integer levelPoints;

    private Integer joinedActivityCount;

    private String inviteCode;

    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private OffsetDateTime updatedAt;

    private static final long serialVersionUID = 1L;
}
