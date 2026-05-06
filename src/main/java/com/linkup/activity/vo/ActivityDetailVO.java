package com.linkup.activity.vo;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

/**
 * 活动详情 VO。
 *
 * <p>这个对象面向活动详情页。
 * 相比列表卡片，它会返回更完整的地点、费用、人数、图片和状态信息。</p>
 */
@Data
@Builder
public class ActivityDetailVO {

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

    /**
     * 消息会话 ID。
     *
     * <p>当前消息模块还没接入，所以这个字段可能为空。
     * 后续接 Firebase 聊天时，详情页可以用它进入活动群聊。</p>
     */
    private String firebaseConversationId;

    /**
     * 活动图片列表。
     *
     * <p>按 sort_order、created_at 排序；如果没有图片，返回空数组。</p>
     */
    private List<Image> images;

    @Data
    @Builder
    public static class Image {

        private UUID id;

        private String url;

        private Integer sortOrder;
    }
}
