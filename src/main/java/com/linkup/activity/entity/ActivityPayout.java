package com.linkup.activity.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linkup.activity.enums.PayoutStatus;
import com.linkup.infrastructure.typehandler.PostgreSQLEnumTypeHandler;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

/**
 * 活动打款记录。
 *
 * <p>对应数据库表 activity_payout。</p>
 */
@Data
@TableName(value = "activity_payout", autoResultMap = true)
public class ActivityPayout implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID activityId;

    private UUID hostId;

    private Integer totalAmount;

    private Integer platformFee;

    private Integer payoutAmount;

    @TableField(value = "status", typeHandler = PostgreSQLEnumTypeHandler.class)
    private PayoutStatus status;

    private String note;

    private OffsetDateTime processedAt;

    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;

    private static final long serialVersionUID = 1L;
}
