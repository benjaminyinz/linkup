package com.linkup.account.user.vo;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * 等级中心信息。
 *
 * <p>这个 VO 对应 material/api_profile(1).md 里的 GET /api/user/level。
 * App“我的”页面可以用它展示当前等级、成长值、参与次数和所有等级档位。</p>
 */
@Data
@Builder
public class UserLevelVO {

    private Integer currentLevel;

    private Integer levelPoints;

    private Integer joinedActivityCount;

    private List<Tier> tiers;

    /**
     * 单个等级档位。
     *
     * <p>目前等级档位是产品固定规则，所以先写在代码里。
     * 后续如果运营需要后台动态调整，再把它迁移到数据库或配置中心。</p>
     */
    @Data
    @Builder
    public static class Tier {

        private Integer id;

        private String name;

        private String tagline;

        private Integer targetPoints;

        private List<String> privileges;
    }
}
