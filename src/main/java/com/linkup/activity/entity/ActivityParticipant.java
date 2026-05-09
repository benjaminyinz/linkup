package com.linkup.activity.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linkup.activity.enums.ParticipantStatus;
import com.linkup.commerce.order.enums.PaymentStatus;
import com.linkup.infrastructure.typehandler.PostgreSQLEnumTypeHandler;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

/**
 * 活动参与记录。
 *
 * <p>对应数据库表 activity_participant。</p>
 */
@Data
@TableName(value = "activity_participant", autoResultMap = true)
public class ActivityParticipant implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID activityId;

    private UUID userId;

    @TableField(value = "status", typeHandler = PostgreSQLEnumTypeHandler.class)
    private ParticipantStatus status;

    private String registrationCode;

    private String paymentReference;

    @TableField(value = "payment_status", typeHandler = PostgreSQLEnumTypeHandler.class)
    private PaymentStatus paymentStatus;

    private Integer amountPaid;

    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime joinedAt;

    private OffsetDateTime cancelledAt;

    private static final long serialVersionUID = 1L;
}
