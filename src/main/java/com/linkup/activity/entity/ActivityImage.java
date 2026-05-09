package com.linkup.activity.entity;

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
 * 活动图片。
 *
 * <p>对应数据库表 activity_image。
 * sort_order = 0 约定为封面图。</p>
 */
@Data
@TableName(value = "activity_image", autoResultMap = true)
public class ActivityImage implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private UUID activityId;

    private String s3Url;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;

    private static final long serialVersionUID = 1L;
}
