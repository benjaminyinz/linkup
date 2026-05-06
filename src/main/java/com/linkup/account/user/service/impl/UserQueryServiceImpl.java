package com.linkup.account.user.service.impl;

import com.linkup.account.user.dto.UserBasicInfoDTO;
import com.linkup.account.user.entity.AppUser;
import com.linkup.account.user.service.AppUserService;
import com.linkup.account.user.service.UserQueryService;
import com.linkup.account.user.vo.UserLevelVO;
import com.linkup.account.user.vo.UserProfileVO;
import com.linkup.common.exception.BusinessException;
import com.linkup.common.result.ResultCodeEnum;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * 用户模块读操作服务实现。
 */
@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final AppUserService appUserService;

    public UserQueryServiceImpl(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @Override
    public UserBasicInfoDTO getBasicInfo(UUID userId) {
        AppUser appUser = appUserService.getById(userId);
        if (appUser == null) {
            return null;
        }

        return new UserBasicInfoDTO(
                toUuid(appUser.getId()),
                appUser.getEmail(),
                appUser.getNickname(),
                appUser.getLevel(),
                appUser.getInviteCode()
        );
    }

    @Override
    public UserProfileVO getProfile(UUID userId) {
        AppUser appUser = appUserService.getById(userId);
        if (appUser == null) {
            throw new BusinessException(ResultCodeEnum.RESOURCE_NOT_FOUND, "用户不存在");
        }

        return toProfileVO(appUser);
    }

    @Override
    public UserLevelVO getLevelInfo(UUID userId) {
        AppUser appUser = getRequiredUser(userId);
        Integer levelPoints = defaultInt(appUser.getLevelPoints());
        Integer joinedActivityCount = defaultInt(appUser.getJoinedActivityCount());

        return UserLevelVO.builder()
                .currentLevel(resolveCurrentLevel(appUser, levelPoints, joinedActivityCount))
                .levelPoints(levelPoints)
                .joinedActivityCount(joinedActivityCount)
                .tiers(buildLevelTiers())
                .build();
    }

    public UserProfileVO toProfileVO(AppUser appUser) {
        Integer joinedActivityCount = defaultInt(appUser.getJoinedActivityCount());

        return UserProfileVO.builder()
                .id(toUuid(appUser.getId()))
                .nickname(appUser.getNickname())
                .avatarUrl(appUser.getAvatarUrl())
                .email(appUser.getEmail())
                .gender(toText(appUser.getGender()))
                .signature(appUser.getSignature())
                .level(defaultInt(appUser.getLevel(), 1))
                .levelPoints(defaultInt(appUser.getLevelPoints()))
                .joinedActivityCount(joinedActivityCount)
                .inviteCode(appUser.getInviteCode())
                .stats(UserProfileVO.Stats.builder()
                        .joinedCount(joinedActivityCount)
                        .metPeopleCount(0)
                        .savedAmount(0)
                        .build())
                .build();
    }

    private AppUser getRequiredUser(UUID userId) {
        AppUser appUser = appUserService.getById(userId);
        if (appUser == null) {
            throw new BusinessException(ResultCodeEnum.RESOURCE_NOT_FOUND, "用户不存在");
        }
        return appUser;
    }

    /**
     * 计算当前等级。
     *
     * <p>数据库里有 level 字段，表示当前等级。
     * 但为了防止早期 MVP 阶段数据还没被定时任务或业务流程及时刷新，
     * 这里会根据规则再算一遍，然后取“数据库等级”和“规则计算等级”中更高的那个。</p>
     */
    private Integer resolveCurrentLevel(AppUser appUser, Integer levelPoints, Integer joinedActivityCount) {
        int levelFromRule = 1;
        if (joinedActivityCount >= 5) {
            levelFromRule = 2;
        }
        if (levelPoints >= 62) {
            levelFromRule = 3;
        }
        if (levelPoints >= 88) {
            levelFromRule = 4;
        }

        return Math.max(defaultInt(appUser.getLevel(), 1), levelFromRule);
    }

    private List<UserLevelVO.Tier> buildLevelTiers() {
        return List.of(
                UserLevelVO.Tier.builder()
                        .id(1)
                        .name("新手")
                        .tagline("先玩局，再发局")
                        .targetPoints(0)
                        .privileges(List.of("新人欢迎券", "可报名普通活动"))
                        .build(),
                UserLevelVO.Tier.builder()
                        .id(2)
                        .name("玩家")
                        .tagline("已解锁：发布免费的局")
                        .targetPoints(62)
                        .privileges(List.of("每月福利券2张", "热门活动优先提醒"))
                        .build(),
                UserLevelVO.Tier.builder()
                        .id(3)
                        .name("达人")
                        .tagline("已解锁：发布付费的局")
                        .targetPoints(88)
                        .privileges(List.of("高阶福利券包", "活动轻曝光加成"))
                        .build(),
                UserLevelVO.Tier.builder()
                        .id(4)
                        .name("局座")
                        .tagline("发局享优先曝光，顶级局座")
                        .targetPoints(100)
                        .privileges(List.of("主理人专属福利包", "活动优先曝光"))
                        .build()
        );
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

    private Integer defaultInt(Integer value) {
        return defaultInt(value, 0);
    }

    private Integer defaultInt(Integer value, Integer defaultValue) {
        return value == null ? defaultValue : value;
    }
}
