package com.linkup.account.user.controller;

import com.linkup.account.user.dto.UpdateUserProfileDTO;
import com.linkup.account.user.service.UserCommandService;
import com.linkup.account.user.service.UserQueryService;
import com.linkup.account.user.vo.UserLevelVO;
import com.linkup.account.user.vo.UserProfileVO;
import com.linkup.common.context.LoginUserContext;
import com.linkup.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户资料接口。
 *
 * <p>这里对应 App“我的”页面里的用户信息区。
 * 接口路径和返回结构按 material/api_profile(1).md 实现。</p>
 */
@Tag(name = "用户资料", description = "我的页面用户资料接口")
@SecurityRequirement(name = "Authorization")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserQueryService userQueryService;

    private final UserCommandService userCommandService;

    public UserController(UserQueryService userQueryService, UserCommandService userCommandService) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
    }

    /**
     * 获取当前用户。
     *
     * <p>JWT 拦截器会先解析 token，并把当前用户 ID 放到 LoginUserContext。
     * 这里直接取当前登录用户 ID 查询资料即可。</p>
     */
    @Operation(summary = "获取当前用户", description = "获取 App 我的页面顶部用户资料")
    @GetMapping("/me")
    public Result<UserProfileVO> getCurrentUser() {
        UUID userId = LoginUserContext.getUserIdRequired();
        return Result.ok(userQueryService.getProfile(userId));
    }

    /**
     * 更新个人资料。
     *
     * <p>所有字段都是可选的。前端只传要修改的字段，后端保留其他字段原值。</p>
     */
    @Operation(summary = "更新个人资料", description = "更新昵称、性别、个性签名")
    @PutMapping("/me")
    public Result<UserProfileVO> updateCurrentUser(@Valid @RequestBody UpdateUserProfileDTO updateUserProfileDTO) {
        UUID userId = LoginUserContext.getUserIdRequired();
        return Result.ok(userCommandService.updateProfile(userId, updateUserProfileDTO));
    }

    /**
     * 获取等级信息。
     *
     * <p>这个接口用于 App“我的”页面里的等级中心。
     * 数据来自 app_user 表里的 level、level_points、joined_activity_count，
     * 等级档位按 material/api_profile(1).md 固定返回。</p>
     */
    @Operation(summary = "获取等级信息", description = "获取当前等级、成长值、参与次数和等级档位")
    @GetMapping("/level")
    public Result<UserLevelVO> getLevelInfo() {
        UUID userId = LoginUserContext.getUserIdRequired();
        return Result.ok(userQueryService.getLevelInfo(userId));
    }
}
