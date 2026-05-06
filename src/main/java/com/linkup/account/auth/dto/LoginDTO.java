package com.linkup.account.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 登录请求参数。
 *
 * <p>登录只需要邮箱和密码。校验放在 DTO 上，可以避免业务代码里到处写
 * if 判断，让 Service 专注处理真正的登录逻辑。</p>
 */
@Data
public class LoginDTO {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱不能超过 100 个字符")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 32, message = "密码长度必须在 8 到 32 位之间")
    private String password;
}
