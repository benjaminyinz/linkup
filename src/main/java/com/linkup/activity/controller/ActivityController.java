package com.linkup.activity.controller;

import com.linkup.activity.dto.ActivityPageQueryDTO;
import com.linkup.activity.service.ActivityService;
import com.linkup.activity.vo.ActivityCardVO;
import com.linkup.activity.vo.ActivityDetailVO;
import com.linkup.common.result.PageResult;
import com.linkup.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 找搭子活动接口。
 *
 * <p>当前先实现只读列表，服务 App 首页/找搭子列表。
 * 暂不实现创建活动、报名、付款等会改变数据或涉及资金的接口。</p>
 */
@Validated
@Tag(name = "找搭子活动", description = "活动列表、活动详情、报名等接口")
@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    /**
     * 获取活动列表。
     *
     * <p>默认只查询 open 状态的活动。
     * 可以通过 status、categoryLabel、dateFrom、dateTo 做简单筛选。</p>
     */
    @Operation(summary = "获取活动列表", description = "分页获取找搭子活动卡片列表，默认查询 open 状态活动")
    @GetMapping
    public Result<PageResult<ActivityCardVO>> pageActivities(@Valid @ModelAttribute ActivityPageQueryDTO queryDTO) {
        return Result.ok(activityService.pageActivities(queryDTO));
    }

    /**
     * 获取活动详情。
     *
     * <p>只读取活动和活动图片，不改变任何业务数据。</p>
     */
    @Operation(summary = "获取活动详情", description = "根据活动 ID 获取活动详情和图片列表")
    @GetMapping("/{id}")
    public Result<ActivityDetailVO> getActivityDetail(@PathVariable UUID id) {
        return Result.ok(activityService.getActivityDetail(id));
    }
}
