package com.linkup.account.auth.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkup.account.auth.dto.LoginDTO;
import com.linkup.account.auth.dto.RegisterDTO;
import com.linkup.account.auth.entity.UserAuth;
import com.linkup.account.auth.mapper.UserAuthMapper;
import com.linkup.account.auth.service.UserAuthService;
import com.linkup.account.auth.support.JwtTokenProvider;
import com.linkup.account.auth.vo.AuthUserVO;
import com.linkup.account.auth.vo.LoginVO;
import com.linkup.common.exception.BusinessException;
import com.linkup.common.result.ResultCodeEnum;
import com.linkup.common.util.UuidUtil;
import com.linkup.account.user.dto.UserBasicInfoDTO;
import com.linkup.account.user.service.UserCommandService;
import com.linkup.account.user.service.UserQueryService;
import java.util.Locale;
import java.util.UUID;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户鉴权基础数据服务实现
 */
@Service
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper, UserAuth>
    implements UserAuthService {

    private final UserCommandService userCommandService;

    private final UserQueryService userQueryService;

    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    public UserAuthServiceImpl(UserCommandService userCommandService,
                               UserQueryService userQueryService,
                               BCryptPasswordEncoder passwordEncoder,
                               JwtTokenProvider jwtTokenProvider) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 邮箱注册。
     *
     * <p>这里会同时写两张表：</p>
     * <ul>
     *     <li>app_user：用户基础资料，例如昵称、邮箱、邀请码。</li>
     *     <li>user_auth：登录凭证，例如登录方式、邮箱、密码哈希。</li>
     * </ul>
     *
     * <p>@Transactional 表示事务。
     * 如果第二张表保存失败，第一张表也会回滚，避免出现“用户资料创建了，但无法登录”的脏数据。</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO register(RegisterDTO registerDTO) {
        String email = normalizeEmail(registerDTO.getEmail());
        String nickname = registerDTO.getNickname().trim();

        if (existsByEmail(email)) {
            throw new BusinessException(ResultCodeEnum.EMAIL_ALREADY_REGISTERED);
        }

        try {
            UUID userId = userCommandService.createEmailUser(email, nickname);

            UserAuth userAuth = new UserAuth();
            userAuth.setId(UuidUtil.newUuidV7());
            userAuth.setUserId(userId);
            userAuth.setProviderUserId(email);
            userAuth.setPasswordHash(passwordEncoder.encode(registerDTO.getPassword()));
            save(userAuth);

            UserBasicInfoDTO userBasicInfo = userQueryService.getBasicInfo(userId);
            if (userBasicInfo == null) {
                throw new BusinessException(ResultCodeEnum.RESOURCE_NOT_FOUND, "用户不存在");
            }
            return buildLoginVO(userBasicInfo);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(ResultCodeEnum.EMAIL_ALREADY_REGISTERED);
        }
    }

    /**
     * 邮箱登录。
     *
     * <p>登录时不会把前端传来的密码拿去和数据库明文比较。
     * 数据库保存的是 BCrypt 哈希，所以要用 passwordEncoder.matches 来校验。</p>
     */
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        String email = normalizeEmail(loginDTO.getEmail());
        UserAuth userAuth = getByEmail(email);

        if (userAuth == null || !StringUtils.hasText(userAuth.getPasswordHash())) {
            throw new BusinessException(ResultCodeEnum.EMAIL_OR_PASSWORD_ERROR);
        }

        boolean passwordMatched = passwordEncoder.matches(loginDTO.getPassword(), userAuth.getPasswordHash());
        if (!passwordMatched) {
            throw new BusinessException(ResultCodeEnum.EMAIL_OR_PASSWORD_ERROR);
        }

        UUID userId = userAuth.getUserId();
        UserBasicInfoDTO userBasicInfo = userQueryService.getBasicInfo(userId);
        if (userBasicInfo == null) {
            throw new BusinessException(ResultCodeEnum.RESOURCE_NOT_FOUND, "用户不存在");
        }

        return buildLoginVO(userBasicInfo);
    }

    /**
     * 根据邮箱查询登录凭证。
     *
     * <p>当前 MVP 只支持邮箱登录，所以先按 provider_user_id 查询。
     * 等以后接 Apple / Google 登录时，再把 provider 一起纳入查询条件。</p>
     */
    private UserAuth getByEmail(String email) {
        return getOne(Wrappers.<UserAuth>lambdaQuery()
                .eq(UserAuth::getProviderUserId, email)
                .last("LIMIT 1"));
    }

    private boolean existsByEmail(String email) {
        return count(Wrappers.<UserAuth>lambdaQuery()
                .eq(UserAuth::getProviderUserId, email)) > 0;
    }

    private LoginVO buildLoginVO(UserBasicInfoDTO userBasicInfo) {
        String token = jwtTokenProvider.createAccessToken(userBasicInfo.userId(), userBasicInfo.email());
        return LoginVO.builder()
                .token(token)
                .user(AuthUserVO.builder()
                        .id(userBasicInfo.userId())
                        .nickname(userBasicInfo.nickname())
                        .email(userBasicInfo.email())
                        .level(userBasicInfo.level())
                        .inviteCode(userBasicInfo.inviteCode())
                        .build())
                .build();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}








