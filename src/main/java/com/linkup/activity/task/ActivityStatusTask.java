package com.linkup.activity.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.linkup.activity.entity.Activity;
import com.linkup.activity.service.ActivityService;
import java.time.LocalDate;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 活动状态自动翻滚。
 *
 * <p>schema 里 activity 没有 end_time 列。当前的产品口径是：
 * 活动一旦过了举办日期（event_date），就视作已经结束，把仍在 open / full 的记录翻成 completed。
 * 这一步不影响 cancelled 状态（host 主动取消）。</p>
 *
 * <p>定时表达式按 NZ 本地时间每天 00:05 跑一次，
 * 这样昨天的活动当天清晨就会全部归档。</p>
 */
@Component
public class ActivityStatusTask {

    private static final Logger log = LoggerFactory.getLogger(ActivityStatusTask.class);

    private static final ZoneId PROJECT_ZONE_ID = ZoneId.of("Pacific/Auckland");

    private final ActivityService activityService;

    public ActivityStatusTask(ActivityService activityService) {
        this.activityService = activityService;
    }

    @Scheduled(cron = "0 5 0 * * *", zone = "Pacific/Auckland")
    public void rollOverPastActivities() {
        LocalDate today = LocalDate.now(PROJECT_ZONE_ID);

        // 用原生 SQL 片段绕开 PG enum 与字符串比较的隐式转换问题：
        // 既显式指定要被翻动的两种状态，也直接把 status 改成 completed。
        boolean changed = activityService.update(null, Wrappers.<Activity>lambdaUpdate()
                .setSql("status = 'completed'::activity_status")
                .lt(Activity::getEventDate, today)
                .apply("status::text IN ('open', 'full')"));

        if (changed) {
            log.info("activity-status-rollover: rolled over past activities for date < {}", today);
        } else {
            log.info("activity-status-rollover: no past activities to roll over for date < {}", today);
        }
    }
}
