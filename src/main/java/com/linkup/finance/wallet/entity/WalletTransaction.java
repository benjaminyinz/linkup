package com.linkup.finance.wallet.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linkup.finance.wallet.enums.WalletRefType;
import com.linkup.finance.wallet.enums.WalletTxStatus;
import com.linkup.finance.wallet.enums.WalletTxType;
import com.linkup.infrastructure.typehandler.PostgreSQLEnumTypeHandler;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

/**
 * 钱包流水。
 *
 * <p>对应数据库表 wallet_transaction。</p>
 */
@Data
@TableName(value = "wallet_transaction", autoResultMap = true)
public class WalletTransaction implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID userId;

    @TableField(value = "type", typeHandler = PostgreSQLEnumTypeHandler.class)
    private WalletTxType type;

    private Integer amount;

    private UUID referenceId;

    @TableField(value = "reference_type", typeHandler = PostgreSQLEnumTypeHandler.class)
    private WalletRefType referenceType;

    @TableField(value = "status", typeHandler = PostgreSQLEnumTypeHandler.class)
    private WalletTxStatus status;

    private String note;

    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;

    private static final long serialVersionUID = 1L;
}
