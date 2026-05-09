package com.linkup.commerce.merchant.entity;

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
 * 团购商家。
 *
 * <p>对应数据库表 merchant。</p>
 */
@Data
@TableName(value = "merchant", autoResultMap = true)
public class Merchant implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private String name;

    private String address;

    private String phone;

    private String hours;

    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private OffsetDateTime updatedAt;

    private static final long serialVersionUID = 1L;
}
