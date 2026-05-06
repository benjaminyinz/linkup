package com.linkup.activity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 活动列表查询参数。
 *
 * <p>这个 DTO 用于 GET /api/activities。
 * 当前只做只读列表，不涉及创建活动、报名、支付等动作。</p>
 */
@Data
@Schema(description = "活动列表查询参数")
public class ActivityPageQueryDTO {

    @Min(value = 1, message = "页码必须大于 0")
    @Schema(description = "页码，从 1 开始", example = "1")
    private Long page = 1L;

    @Min(value = 1, message = "每页数量必须大于 0")
    @Max(value = 50, message = "每页数量不能超过 50")
    @Schema(description = "每页数量，最大 50", example = "20")
    private Long pageSize = 20L;

    @Pattern(regexp = "open|full|completed|cancelled", message = "活动状态不正确")
    @Schema(description = "活动状态：open / full / completed / cancelled，默认 open", example = "open")
    private String status = "open";

    @Schema(description = "分类标签，例如 羽毛球 / 火锅 / 电影", example = "羽毛球")
    private String categoryLabel;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "开始日期，格式 yyyy-MM-dd", example = "2026-05-06")
    private LocalDate dateFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "结束日期，格式 yyyy-MM-dd", example = "2026-05-31")
    private LocalDate dateTo;
}
