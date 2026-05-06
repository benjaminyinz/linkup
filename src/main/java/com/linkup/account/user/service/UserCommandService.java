package com.linkup.account.user.service;

import com.linkup.account.user.dto.UpdateUserProfileDTO;
import com.linkup.account.user.vo.UserProfileVO;
import java.util.UUID;

/**
 * 用户模块对外提供的写操作服务。
 *
 * <p>按照我们前面定的模块规则：app_user 表归 User 模块拥有。
 * 其他模块如果要创建或修改用户，不直接操作 AppUserMapper，而是调用这里。</p>
 */
public interface UserCommandService {

    /**
     * 创建邮箱注册用户。
     *
     * <p>Auth 模块处理“怎么登录”，User 模块处理“用户基础资料怎么落库”。
     * 所以注册流程里，AuthService 会调用这个方法创建 app_user 记录。</p>
     *
     * @param email 邮箱
     * @param nickname 昵称
     * @return 新用户 ID
     */
    UUID createEmailUser(String email, String nickname);

    /**
     * 更新当前用户个人资料。
     *
     * <p>只更新前端传入的字段；没有传的字段保持原值。</p>
     *
     * @param userId 当前登录用户 ID
     * @param updateUserProfileDTO 更新参数
     * @return 更新后的我的页面用户资料
     */
    UserProfileVO updateProfile(UUID userId, UpdateUserProfileDTO updateUserProfileDTO);
}
