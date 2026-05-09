package com.linkup.growth.invite.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linkup.growth.invite.enums.InviteStatus;
import com.linkup.infrastructure.typehandler.PostgreSQLEnumTypeHandler;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

/**
 * 邀请记录。
 *
 * <p>对应数据库表 invite_record。</p>
 */
@Data
@TableName(value = "invite_record", autoResultMap = true)
public class InviteRecord implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID inviterId;

    private UUID inviteeId;

    @TableField(value = "status", typeHandler = PostgreSQLEnumTypeHandler.class)
    private InviteStatus status;

    private UUID inviteeFirstOrderId;

    private Integer rewardAmount;

    private OffsetDateTime rewardedAt;

    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;

    private static final long serialVersionUID = 1L;
}
