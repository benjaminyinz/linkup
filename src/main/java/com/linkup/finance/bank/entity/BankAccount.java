package com.linkup.finance.bank.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

/**
 * 用户绑定的银行账户。
 *
 * <p>对应数据库表 bank_account。
 * 用于 host 提现，每个用户仅允许绑定一个账户。</p>
 */
@Data
@TableName(value = "bank_account", autoResultMap = true)
public class BankAccount implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID userId;

    private String accountName;

    private String accountNumber;

    private String bankName;

    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private OffsetDateTime updatedAt;

    private static final long serialVersionUID = 1L;
}
