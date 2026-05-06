package com.linkup.account.user.service;

import com.linkup.account.user.dto.UserBasicInfoDTO;
import com.linkup.account.user.vo.UserLevelVO;
import com.linkup.account.user.vo.UserProfileVO;
import java.util.UUID;

/**
 * 用户模块对外提供的读操作服务。
 *
 * <p>其他模块需要读取用户基础信息时，优先调用这里，
 * 不直接调用 AppUserMapper，也不直接依赖 AppUser Entity。</p>
 */
public interface UserQueryService {

    /**
     * 根据用户 ID 查询用户基础信息。
     *
     * @param userId 用户 ID
     * @return 用户基础信息；如果用户不存在，返回 null
     */
    UserBasicInfoDTO getBasicInfo(UUID userId);

    /**
     * 查询我的页面用户资料。
     *
     * @param userId 当前登录用户 ID
     * @return 我的页面用户资料
     */
    UserProfileVO getProfile(UUID userId);

    /**
     * 查询等级中心信息。
     *
     * @param userId 当前登录用户 ID
     * @return 等级中心信息
     */
    UserLevelVO getLevelInfo(UUID userId);
}
