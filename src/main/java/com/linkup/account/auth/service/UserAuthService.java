package com.linkup.account.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linkup.account.auth.dto.LoginDTO;
import com.linkup.account.auth.dto.RegisterDTO;
import com.linkup.account.auth.entity.UserAuth;
import com.linkup.account.auth.vo.LoginVO;

/**
 * 用户鉴权基础数据服务
 */
public interface UserAuthService extends IService<UserAuth> {

    /**
     * 邮箱注册。
     *
     * <p>注册成功后直接返回登录态，前端可以保存 token，
     * 不需要让用户注册完再手动登录一次。</p>
     *
     * @param registerDTO 注册请求参数
     * @return 登录成功信息
     */
    LoginVO register(RegisterDTO registerDTO);

    /**
     * 邮箱登录。
     *
     * @param loginDTO 登录请求参数
     * @return 登录成功信息
     */
    LoginVO login(LoginDTO loginDTO);
}




