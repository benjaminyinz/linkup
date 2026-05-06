package com.linkup.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkup.activity.dto.ActivityPageQueryDTO;
import com.linkup.activity.entity.Activity;
import com.linkup.activity.entity.ActivityImage;
import com.linkup.activity.mapper.ActivityImageMapper;
import com.linkup.activity.mapper.ActivityMapper;
import com.linkup.activity.service.ActivityService;
import com.linkup.activity.vo.ActivityCardVO;
import com.linkup.activity.vo.ActivityDetailVO;
import com.linkup.common.exception.BusinessException;
import com.linkup.common.result.PageResult;
import com.linkup.common.result.ResultCodeEnum;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 活动基础数据服务实现
 */
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity>
    implements ActivityService {

    private static final ZoneId PROJECT_ZONE_ID = ZoneId.of("Pacific/Auckland");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ActivityImageMapper activityImageMapper;

    public ActivityServiceImpl(ActivityImageMapper activityImageMapper) {
        this.activityImageMapper = activityImageMapper;
    }

    @Override
    public PageResult<ActivityCardVO> pageActivities(ActivityPageQueryDTO queryDTO) {
        Page<Activity> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        LambdaQueryWrapper<Activity> wrapper = buildPageWrapper(queryDTO);
        Page<Activity> activityPage = page(page, wrapper);

        List<Activity> activities = activityPage.getRecords();
        Map<UUID, String> coverImageMap = getCoverImageMap(activities);

        List<ActivityCardVO> records = activities.stream()
                .map(activity -> toCardVO(activity, coverImageMap.get(toUuid(activity.getId()))))
                .toList();

        return PageResult.<ActivityCardVO>builder()
                .total(activityPage.getTotal())
                .page(activityPage.getCurrent())
                .pageSize(activityPage.getSize())
                .records(records)
                .build();
    }

    @Override
    public ActivityDetailVO getActivityDetail(UUID id) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new BusinessException(ResultCodeEnum.RESOURCE_NOT_FOUND, "活动不存在");
        }

        List<ActivityImage> images = getActivityImages(id);
        return toDetailVO(activity, images);
    }

    private LambdaQueryWrapper<Activity> buildPageWrapper(ActivityPageQueryDTO queryDTO) {
        LambdaQueryWrapper<Activity> wrapper = Wrappers.lambdaQuery();

        if (StringUtils.hasText(queryDTO.getStatus())) {
            // PostgreSQL enum 字段和字符串参数比较时需要显式转换类型。
            wrapper.apply("status = {0}::activity_status", queryDTO.getStatus());
        }
        if (StringUtils.hasText(queryDTO.getCategoryLabel())) {
            wrapper.eq(Activity::getCategoryLabel, queryDTO.getCategoryLabel().trim());
        }
        if (queryDTO.getDateFrom() != null) {
            wrapper.ge(Activity::getEventDate, java.sql.Date.valueOf(queryDTO.getDateFrom()));
        }
        if (queryDTO.getDateTo() != null) {
            wrapper.le(Activity::getEventDate, java.sql.Date.valueOf(queryDTO.getDateTo()));
        }

        wrapper.orderByAsc(Activity::getEventDate)
                .orderByAsc(Activity::getStartTime)
                .orderByDesc(Activity::getCreatedAt);
        return wrapper;
    }

    /**
     * 批量查询活动封面图。
     *
     * <p>sort_order = 0 约定为封面图。
     * 这里一次性查出当前页所有活动的图片，再按 activityId 分组，避免列表 N+1 查询。</p>
     */
    private Map<UUID, String> getCoverImageMap(List<Activity> activities) {
        if (activities.isEmpty()) {
            return Map.of();
        }

        List<Object> activityIds = activities.stream()
                .map(Activity::getId)
                .toList();

        List<ActivityImage> images = activityImageMapper.selectList(Wrappers.<ActivityImage>lambdaQuery()
                .in(ActivityImage::getActivityId, activityIds)
                .orderByAsc(ActivityImage::getSortOrder)
                .orderByAsc(ActivityImage::getCreatedAt));

        return images.stream()
                .collect(Collectors.toMap(
                        image -> toUuid(image.getActivityId()),
                        ActivityImage::getS3Url,
                        (first, ignored) -> first,
                        LinkedHashMap::new
                ));
    }

    private ActivityCardVO toCardVO(Activity activity, String coverImageUrl) {
        return ActivityCardVO.builder()
                .id(toUuid(activity.getId()))
                .hostId(toUuid(activity.getHostId()))
                .title(activity.getTitle())
                .emoji(activity.getEmoji())
                .description(activity.getDescription())
                .categoryLabel(activity.getCategoryLabel())
                .styleKey(activity.getStyleKey())
                .eventDate(formatDate(activity.getEventDate()))
                .startTime(formatTime(activity.getStartTime()))
                .locationName(activity.getLocationName())
                .locationDetail(activity.getLocationDetail())
                .latitude(activity.getLatitude())
                .longitude(activity.getLongitude())
                .maxParticipants(activity.getMaxParticipants())
                .currentParticipants(activity.getCurrentParticipants())
                .feeType(toText(activity.getFeeType()))
                .price(activity.getPrice())
                .feeDetail(activity.getFeeDetail())
                .ctaText(activity.getCtaText())
                .status(toText(activity.getStatus()))
                .coverImageUrl(coverImageUrl)
                .build();
    }

    private ActivityDetailVO toDetailVO(Activity activity, List<ActivityImage> images) {
        return ActivityDetailVO.builder()
                .id(toUuid(activity.getId()))
                .hostId(toUuid(activity.getHostId()))
                .title(activity.getTitle())
                .emoji(activity.getEmoji())
                .description(activity.getDescription())
                .categoryLabel(activity.getCategoryLabel())
                .styleKey(activity.getStyleKey())
                .eventDate(formatDate(activity.getEventDate()))
                .startTime(formatTime(activity.getStartTime()))
                .locationName(activity.getLocationName())
                .locationDetail(activity.getLocationDetail())
                .latitude(activity.getLatitude())
                .longitude(activity.getLongitude())
                .maxParticipants(activity.getMaxParticipants())
                .currentParticipants(activity.getCurrentParticipants())
                .feeType(toText(activity.getFeeType()))
                .price(activity.getPrice())
                .feeDetail(activity.getFeeDetail())
                .ctaText(activity.getCtaText())
                .status(toText(activity.getStatus()))
                .firebaseConversationId(activity.getFirebaseConversationId())
                .images(images.stream()
                        .map(this::toImageVO)
                        .toList())
                .build();
    }

    private ActivityDetailVO.Image toImageVO(ActivityImage image) {
        return ActivityDetailVO.Image.builder()
                .id(toUuid(image.getId()))
                .url(image.getS3Url())
                .sortOrder(image.getSortOrder())
                .build();
    }

    private List<ActivityImage> getActivityImages(UUID activityId) {
        return activityImageMapper.selectList(Wrappers.<ActivityImage>lambdaQuery()
                .eq(ActivityImage::getActivityId, activityId)
                .orderByAsc(ActivityImage::getSortOrder)
                .orderByAsc(ActivityImage::getCreatedAt));
    }

    private UUID toUuid(Object value) {
        if (value instanceof UUID uuid) {
            return uuid;
        }
        return UUID.fromString(String.valueOf(value));
    }

    private String toText(Object value) {
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    private String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        if (date instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate().format(DATE_FORMATTER);
        }
        LocalDate localDate = date.toInstant().atZone(PROJECT_ZONE_ID).toLocalDate();
        return localDate.format(DATE_FORMATTER);
    }

    private String formatTime(Date time) {
        if (time == null) {
            return null;
        }
        if (time instanceof Time sqlTime) {
            return sqlTime.toLocalTime().format(TIME_FORMATTER);
        }
        LocalTime localTime = time.toInstant().atZone(PROJECT_ZONE_ID).toLocalTime();
        return localTime.format(TIME_FORMATTER);
    }

}








