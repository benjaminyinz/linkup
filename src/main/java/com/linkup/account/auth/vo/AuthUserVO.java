package com.linkup.account.auth.vo;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

/**
 * 注册/登录成功后返回的用户摘要。
 *
 * <p>这个结构来自 material/api_profile(1).md。
 * Auth 接口只返回前端登录后立刻需要的少量用户信息；
 * 更完整的个人资料由 GET /api/user/me 返回。</p>
 */
@Data
@Builder
public class AuthUserVO {

    private UUID id;

    private String nickname;

    private String email;

    private Integer level;

    private String inviteCode;
}
