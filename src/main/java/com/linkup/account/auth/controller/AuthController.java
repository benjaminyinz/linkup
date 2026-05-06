package com.linkup.account.auth.controller;

import com.linkup.account.auth.dto.LoginDTO;
import com.linkup.account.auth.dto.RegisterDTO;
import com.linkup.account.auth.service.UserAuthService;
import com.linkup.account.auth.vo.LoginVO;
import com.linkup.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口。
 *
 * <p>Controller 是 HTTP 入口层。
 * 它负责接收前端请求、触发参数校验、调用 Service，然后把结果按统一 Result 格式返回。</p>
 *
 * <p>真正的注册/登录业务逻辑不写在 Controller 里，
 * 而是放在 UserAuthService，避免 Controller 越写越臃肿。</p>
 */
@Tag(name = "认证模块", description = "注册、登录、token 签发相关接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserAuthService userAuthService;

    public AuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    /**
     * 邮箱注册。
     *
     * <p>@Valid 会触发 RegisterDTO 上的校验注解。
     * 如果邮箱格式不对、密码太短、昵称为空，请求不会进入 Service，
     * 会直接被 GlobalExceptionHandler 统一返回参数错误。</p>
     */
    @Operation(summary = "邮箱注册", description = "使用邮箱、密码、昵称创建用户。注册成功后直接返回 token。")
    @PostMapping("/register")
    public Result<LoginVO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return Result.ok(userAuthService.register(registerDTO));
    }

    /**
     * 邮箱登录。
     *
     * <p>登录成功后，前端保存 token。
     * 后续访问需要登录的接口时，在请求头里带上：
     * Authorization: Bearer token。</p>
     */
    @Operation(summary = "邮箱登录", description = "使用邮箱和密码登录。登录成功后返回 token。")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.ok(userAuthService.login(loginDTO));
    }
}
