package com.linkup.activity.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linkup.activity.enums.ActivityFeeType;
import com.linkup.activity.enums.ActivityStatus;
import com.linkup.infrastructure.typehandler.PostgreSQLEnumTypeHandler;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

/**
 * 找搭子活动。
 *
 * <p>对应数据库表 activity。</p>
 */
@Data
@TableName(value = "activity", autoResultMap = true)
public class Activity implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID hostId;

    private String title;

    private String emoji;

    private String description;

    private String categoryLabel;

    private String styleKey;

    private LocalDate eventDate;

    /** NZ 本地时间，无时区。 */
    private LocalTime startTime;

    private String locationName;

    private String locationDetail;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Integer maxParticipants;

    private Integer currentParticipants;

    @TableField(value = "fee_type", typeHandler = PostgreSQLEnumTypeHandler.class)
    private ActivityFeeType feeType;

    private Integer price;

    private String feeDetail;

    private String ctaText;

    @TableField(value = "status", typeHandler = PostgreSQLEnumTypeHandler.class)
    private ActivityStatus status;

    private String firebaseConversationId;

    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private OffsetDateTime updatedAt;

    private static final long serialVersionUID = 1L;
}
