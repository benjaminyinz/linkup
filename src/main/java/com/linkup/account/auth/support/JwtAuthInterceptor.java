package com.linkup.account.auth.support;

import com.linkup.common.context.LoginUserContext;
import com.linkup.common.exception.BusinessException;
import com.linkup.common.result.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 登录拦截器。
 *
 * <p>它会在 Controller 执行之前运行，负责完成下面几件事：</p>
 *
 * <ul>
 *     <li>从请求头 Authorization 里读取 token。</li>
 *     <li>调用 JwtTokenProvider 校验 token 是否有效。</li>
 *     <li>解析出 userId，并保存到 LoginUserContext。</li>
 *     <li>请求结束后清理 LoginUserContext。</li>
 * </ul>
 *
 * <p>拦截器只做“识别当前用户”这件事。
 * 它不查询订单、不判断活动权限，也不处理业务逻辑。</p>
 */
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String OPTIONS_METHOD = "OPTIONS";

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Controller 执行前调用。
     *
     * <p>返回 true 表示继续执行 Controller。
     * 如果 token 缺失或无效，会抛出 BusinessException，统一异常处理器会返回未登录 JSON。</p>
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (OPTIONS_METHOD.equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(authorization)) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED);
        }

        JwtTokenProvider.JwtUserInfo jwtUserInfo = jwtTokenProvider.parseAccessToken(authorization);
        LoginUserContext.set(new LoginUserContext.LoginUser(jwtUserInfo.userId(), jwtUserInfo.email()));
        return true;
    }

    /**
     * 请求结束后调用。
     *
     * <p>无论 Controller 正常返回，还是中途抛异常，只要请求进入了 Controller 链路，
     * Spring 都会尽量调用这里。这里必须清理 ThreadLocal，防止线程复用导致用户串号。</p>
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        LoginUserContext.clear();
    }
}
