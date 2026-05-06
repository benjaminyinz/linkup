package com.linkup.account.auth.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 登录成功后返回给前端的数据。
 *
 * <p>VO 是后端返回给前端看的对象。
 * 它不等于数据库 Entity，只放前端当前需要展示或保存的数据。</p>
 */
@Data
@Builder
public class LoginVO {

    /**
     * JWT token。
     *
     * <p>字段名按 material/api_profile(1).md 保持为 token。
     * 前端请求需要登录的接口时，把它放到 Authorization 请求头里：</p>
     * <pre>
     * Authorization: Bearer token
     * </pre>
     */
    private String token;

    /**
     * 当前登录用户摘要。
     */
    private AuthUserVO user;
}
