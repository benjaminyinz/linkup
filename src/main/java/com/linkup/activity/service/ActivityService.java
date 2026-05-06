package com.linkup.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linkup.activity.dto.ActivityPageQueryDTO;
import com.linkup.activity.entity.Activity;
import com.linkup.activity.vo.ActivityCardVO;
import com.linkup.activity.vo.ActivityDetailVO;
import com.linkup.common.result.PageResult;
import java.util.UUID;

/**
 * 活动基础数据服务
 */
public interface ActivityService extends IService<Activity> {

    /**
     * 分页查询活动列表。
     *
     * <p>当前只做只读列表，不处理报名、支付、聊天等动作。</p>
     *
     * @param queryDTO 查询参数
     * @return 活动卡片分页结果
     */
    PageResult<ActivityCardVO> pageActivities(ActivityPageQueryDTO queryDTO);

    /**
     * 获取活动详情。
     *
     * @param id 活动 ID
     * @return 活动详情
     */
    ActivityDetailVO getActivityDetail(UUID id);
}




