package com.linkup.account.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.linkup.account.user.dto.UpdateUserProfileDTO;
import com.linkup.account.user.entity.AppUser;
import com.linkup.account.user.enums.GenderType;
import com.linkup.account.user.service.AppUserService;
import com.linkup.account.user.service.UserCommandService;
import com.linkup.account.user.service.UserQueryService;
import com.linkup.account.user.vo.UserProfileVO;
import com.linkup.common.exception.BusinessException;
import com.linkup.common.result.ResultCodeEnum;
import com.linkup.common.util.UuidUtil;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户模块写操作服务实现。
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {

    private static final int INVITE_CODE_RANDOM_LENGTH = 8;

    private final AppUserService appUserService;

    private final UserQueryService userQueryService;

    public UserCommandServiceImpl(AppUserService appUserService, UserQueryService userQueryService) {
        this.appUserService = appUserService;
        this.userQueryService = userQueryService;
    }

    @Override
    public UUID createEmailUser(String email, String nickname) {
        UUID userId = UuidUtil.newUuidV7();

        AppUser appUser = new AppUser();
        appUser.setId(userId);
        appUser.setEmail(email);
        appUser.setNickname(nickname);
        appUser.setInviteCode(generateUniqueInviteCode());

        appUserService.save(appUser);
        return userId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserProfileVO updateProfile(UUID userId, UpdateUserProfileDTO updateUserProfileDTO) {
        AppUser appUser = appUserService.getById(userId);
        if (appUser == null) {
            throw new BusinessException(ResultCodeEnum.RESOURCE_NOT_FOUND, "用户不存在");
        }

        if (updateUserProfileDTO.getNickname() != null) {
            String nickname = updateUserProfileDTO.getNickname().trim();
            if (!StringUtils.hasText(nickname)) {
                throw new BusinessException(ResultCodeEnum.PARAM_VALIDATE_FAILED, "昵称不能为空");
            }
            appUser.setNickname(nickname);
        }

        if (updateUserProfileDTO.getGender() != null) {
            appUser.setGender(GenderType.fromValue(updateUserProfileDTO.getGender()));
        }

        if (updateUserProfileDTO.getSignature() != null) {
            appUser.setSignature(updateUserProfileDTO.getSignature().trim());
        }

        appUserService.updateById(appUser);
        return userQueryService.getProfile(userId);
    }

    /**
     * 生成唯一邀请码。
     *
     * <p>数据库已经给 invite_code 加了唯一约束，这里提前查重是为了减少插入时报错。
     * 真正的最终防线仍然是数据库唯一索引。</p>
     */
    private String generateUniqueInviteCode() {
        for (int i = 0; i < 10; i++) {
            String inviteCode = "LK" + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, INVITE_CODE_RANDOM_LENGTH)
                    .toUpperCase(Locale.ROOT);

            long count = appUserService.count(Wrappers.<AppUser>lambdaQuery()
                    .eq(AppUser::getInviteCode, inviteCode));
            if (count == 0) {
                return inviteCode;
            }
        }

        return "LK" + UuidUtil.newUuidV7()
                .toString()
                .replace("-", "")
                .substring(0, INVITE_CODE_RANDOM_LENGTH)
                .toUpperCase(Locale.ROOT);
    }
}
