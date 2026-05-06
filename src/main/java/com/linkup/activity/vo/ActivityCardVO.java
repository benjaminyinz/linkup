package com.linkup.activity.vo;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

/**
 * 活动列表卡片 VO。
 *
 * <p>这个对象面向 App 首页/找搭子列表卡片。
 * 它不是数据库 Entity，只放前端列表页需要展示的信息。</p>
 */
@Data
@Builder
public class ActivityCardVO {

    private UUID id;

    private UUID hostId;

    private String title;

    private String emoji;

    private String description;

    private String categoryLabel;

    private String styleKey;

    private String eventDate;

    private String startTime;

    private String locationName;

    private String locationDetail;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Integer maxParticipants;

    private Integer currentParticipants;

    private String feeType;

    private Integer price;

    private String feeDetail;

    private String ctaText;

    private String status;

    private String coverImageUrl;
}
