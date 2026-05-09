package com.linkup.growth.voucher.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linkup.growth.voucher.enums.VoucherSource;
import com.linkup.growth.voucher.enums.VoucherStatus;
import com.linkup.infrastructure.typehandler.PostgreSQLEnumTypeHandler;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

/**
 * 优惠券。
 *
 * <p>对应数据库表 voucher。</p>
 */
@Data
@TableName(value = "voucher", autoResultMap = true)
public class Voucher implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID userId;

    private Integer amount;

    private String rule;

    private String title;

    private String subtitle;

    @TableField(value = "source", typeHandler = PostgreSQLEnumTypeHandler.class)
    private VoucherSource source;

    @TableField(value = "status", typeHandler = PostgreSQLEnumTypeHandler.class)
    private VoucherStatus status;

    private OffsetDateTime expiresAt;

    private OffsetDateTime usedAt;

    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;

    private static final long serialVersionUID = 1L;
}
