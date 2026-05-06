package com.linkup.common.context;

import com.linkup.common.exception.BusinessException;
import com.linkup.common.result.ResultCodeEnum;
import java.util.UUID;

/**
 * 当前登录用户上下文。
 *
 * <p>它的作用是：在一次 HTTP 请求处理过程中，保存“当前是谁在访问接口”。</p>
 *
 * <p>为什么不用每个 Service 方法都传 userId？因为后续很多业务都会用到当前用户：</p>
 * <ul>
 *     <li>创建找搭子活动：hostId 就是当前登录用户 ID。</li>
 *     <li>购买团购订单：userId 就是当前登录用户 ID。</li>
 *     <li>查看我的订单：只查询当前登录用户的数据。</li>
 * </ul>
 *
 * <p>ThreadLocal 可以理解成“当前线程自己的小盒子”。
 * Spring MVC 处理一次请求时，当前请求在线程中执行；我们把 userId 放进去，
 * 后面的 Controller / Service 就能在同一次请求里拿到它。</p>
 *
 * <p>非常重要：请求结束后必须调用 clear 清理。
 * 如果不清理，线程被复用时可能把上一个用户的信息带到下一个请求里。</p>
 */
public final class LoginUserContext {

    private static final ThreadLocal<LoginUser> LOGIN_USER_HOLDER = new ThreadLocal<>();

    private LoginUserContext() {
    }

    /**
     * 保存当前登录用户。
     *
     * <p>这个方法通常只在 JWT 拦截器里调用。
     * 业务代码不应该自己随便 set，避免伪造当前用户。</p>
     */
    public static void set(LoginUser loginUser) {
        LOGIN_USER_HOLDER.set(loginUser);
    }

    /**
     * 获取当前登录用户。
     *
     * @return 当前登录用户；如果当前接口没有经过登录拦截器，可能返回 null
     */
    public static LoginUser get() {
        return LOGIN_USER_HOLDER.get();
    }

    /**
     * 获取当前登录用户 ID。
     *
     * <p>适合在已经确认必须登录的业务接口中使用。
     * 如果没有登录信息，会抛出统一的未登录异常。</p>
     */
    public static UUID getUserIdRequired() {
        LoginUser loginUser = get();
        if (loginUser == null || loginUser.userId() == null) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED);
        }
        return loginUser.userId();
    }

    /**
     * 清理当前线程里的登录用户。
     *
     * <p>JWT 拦截器会在请求结束时调用它。
     * 这一步是 ThreadLocal 使用中的安全底线。</p>
     */
    public static void clear() {
        LOGIN_USER_HOLDER.remove();
    }

    /**
     * 当前登录用户信息。
     *
     * <p>这里只放非常基础的信息。
     * 如果业务需要用户等级、状态、权限，应该根据 userId 查询数据库或缓存，
     * 不要把很多易变数据都塞进 JWT 和 ThreadLocal。</p>
     */
    public record LoginUser(UUID userId, String email) {
    }
}
