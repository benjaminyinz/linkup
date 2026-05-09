package com.linkup.commerce.deal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.UUID;
import lombok.Data;

/**
 * 团购使用须知条目。
 *
 * <p>对应数据库表 deal_terms。</p>
 */
@Data
@TableName(value = "deal_terms", autoResultMap = true)
public class DealTerms implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID dealId;

    private String content;

    private Integer sortOrder;

    private static final long serialVersionUID = 1L;
}
