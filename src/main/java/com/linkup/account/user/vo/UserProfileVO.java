package com.linkup.account.user.vo;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

/**
 * 我的页面用户资料。
 *
 * <p>这个 VO 对应 material/api_profile(1).md 里的 GET /api/user/me。
 * 它是 App“我的”页面顶部用户信息区需要的数据。</p>
 */
@Data
@Builder
public class UserProfileVO {

    private UUID id;

    private String nickname;

    private String avatarUrl;

    private String email;

    private String gender;

    private String signature;

    private Integer level;

    private Integer levelPoints;

    private Integer joinedActivityCount;

    private String inviteCode;

    private Stats stats;

    /**
     * 我的页面统计信息。
     *
     * <p>joinedCount 先复用 app_user.joined_activity_count。
     * metPeopleCount 和 savedAmount 需要活动参与、订单优惠等业务继续完善后才能精确计算，
     * 目前 MVP 阶段先返回 0，保持接口结构稳定。</p>
     */
    @Data
    @Builder
    public static class Stats {

        private Integer joinedCount;

        private Integer metPeopleCount;

        private Integer savedAmount;
    }
}
