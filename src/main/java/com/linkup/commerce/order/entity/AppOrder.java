package com.linkup.commerce.order.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linkup.commerce.order.enums.PaymentStatus;
import com.linkup.commerce.order.enums.RedeemStatus;
import com.linkup.commerce.order.enums.RefundStatus;
import com.linkup.infrastructure.typehandler.PostgreSQLEnumTypeHandler;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

/**
 * 团购订单。
 *
 * <p>对应数据库表 app_order，避开 PostgreSQL 保留字 order。</p>
 */
@Data
@TableName(value = "app_order", autoResultMap = true)
public class AppOrder implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID userId;

    private UUID dealId;

    private String orderNo;

    private String paymentNo;

    private Integer amountPaid;

    private String paymentReference;

    @TableField(value = "payment_status", typeHandler = PostgreSQLEnumTypeHandler.class)
    private PaymentStatus paymentStatus;

    private String redeemCode;

    @TableField(value = "redeem_status", typeHandler = PostgreSQLEnumTypeHandler.class)
    private RedeemStatus redeemStatus;

    private OffsetDateTime redeemedAt;

    @TableField(value = "refund_status", typeHandler = PostgreSQLEnumTypeHandler.class)
    private RefundStatus refundStatus;

    private String refundReason;

    private String afterSaleNote;

    private String usageWindow;

    private String usageRule;

    /**
     * 购买时固化的套餐快照，对应 PG JSONB 列。
     *
     * <p>读出来是 JSON 文本；后续若要做结构化访问，再切到 JacksonTypeHandler / 自定义 POJO。</p>
     */
    private String packageSnapshot;

    private OffsetDateTime expiresAt;

    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;

    private static final long serialVersionUID = 1L;
}
