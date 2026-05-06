package com.linkup.account.user.dto;

import java.util.UUID;

/**
 * 用户基础信息 DTO。
 *
 * <p>这个对象用于模块之间传递用户基础信息。
 * 其他模块只需要知道 userId、email、nickname，不应该直接拿 AppUser Entity。</p>
 */
public record UserBasicInfoDTO(
        UUID userId,
        String email,
        String nickname,
        Integer level,
        String inviteCode
) {
}
