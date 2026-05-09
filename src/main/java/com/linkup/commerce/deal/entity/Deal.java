package com.linkup.commerce.deal.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linkup.commerce.deal.enums.DealSegment;
import com.linkup.commerce.deal.enums.DealStatus;
import com.linkup.infrastructure.typehandler.PostgreSQLEnumTypeHandler;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

/**
 * 团购套餐。
 *
 * <p>对应数据库表 deal。</p>
 */
@Data
@TableName(value = "deal", autoResultMap = true)
public class Deal implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID merchantId;

    @TableField(value = "segment", typeHandler = PostgreSQLEnumTypeHandler.class)
    private DealSegment segment;

    private String styleKey;

    private String title;

    private String shortTitle;

    private String subtitle;

    private String emoji;

    private String badge;

    private Integer price;

    private Integer originalPrice;

    private String discountText;

    private String description;

    private Integer validityDays;

    private String includes;

    private String refundPolicy;

    private String locationArea;

    private Integer soldCount;

    @TableField(value = "status", typeHandler = PostgreSQLEnumTypeHandler.class)
    private DealStatus status;

    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private OffsetDateTime updatedAt;

    private static final long serialVersionUID = 1L;
}
