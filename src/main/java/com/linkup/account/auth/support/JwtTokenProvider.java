package com.linkup.account.auth.support;

import com.linkup.account.auth.config.JwtProperties;
import com.linkup.common.exception.BusinessException;
import com.linkup.common.result.ResultCodeEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * JWT token 生成和解析工具。
 *
 * <p>它和 example 里的 JwtUtil / JwtHelper 作用类似，但这里按当前项目的生产环境方式处理：</p>
 *
 * <ul>
 *     <li>使用项目当前依赖的 JJWT 0.12.x 新版 API。</li>
 *     <li>密钥、签发方、过期时间来自配置文件，不写死在代码里。</li>
 *     <li>解析失败时抛出 BusinessException，交给 GlobalExceptionHandler 统一返回 JSON。</li>
 * </ul>
 *
 * <p>注意：这个类只负责 token 本身。
 * 它不负责查询数据库，也不负责判断用户是否被封禁。
 * 这些业务判断后续应该放到 AuthService 或 JWT 拦截器里。</p>
 */
@Component
public class JwtTokenProvider {

    /**
     * token 里保存用户 ID 的 claim 名称。
     *
     * <p>JWT 有标准字段 subject，但为了让前端或排查日志时更直观，
     * 我们也额外放一份 userId。</p>
     */
    private static final String CLAIM_USER_ID = "userId";

    /**
     * token 里保存邮箱的 claim 名称。
     *
     * <p>邮箱不是权限判断的唯一依据，只是方便前端展示和后端排查。
     * 真正需要强一致的用户信息时，仍然应该根据 userId 查询数据库。</p>
     */
    private static final String CLAIM_EMAIL = "email";

    /**
     * HTTP Authorization 请求头里 Bearer token 的固定前缀。
     *
     * <p>前端通常会这样传：</p>
     * <pre>
     * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
     * </pre>
     */
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProperties properties;

    /**
     * JJWT 用来签名和验签的密钥对象。
     *
     * <p>构造方法里提前创建好，避免每次生成/解析 token 都重复处理密钥字符串。</p>
     */
    private final SecretKey signingKey;

    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
        this.signingKey = buildSigningKey(properties.getSecret());
    }

    /**
     * 创建 access token。
     *
     * <p>注册成功、登录成功后，AuthService 可以调用这个方法生成 token 返回给前端。
     * 前端之后访问需要登录的接口时，把 token 放到 Authorization 请求头里。</p>
     *
     * @param userId 当前登录用户的 ID
     * @param email 当前登录用户的邮箱
     * @return 后端签发的 JWT 字符串
     */
    public String createAccessToken(UUID userId, String email) {
        return createAccessToken(userId.toString(), email);
    }

    /**
     * 创建 access token。
     *
     * <p>这里额外提供 String 版本，是为了兼容当前部分 Entity 里 ID 还是 Object 的过渡状态。
     * 等实体 ID 类型全部整理成 UUID 后，业务代码优先使用 UUID 版本即可。</p>
     *
     * @param userId 当前登录用户的 ID 字符串
     * @param email 当前登录用户的邮箱
     * @return 后端签发的 JWT 字符串
     */
    public String createAccessToken(String userId, String email) {
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ResultCodeEnum.PARAM_VALIDATE_FAILED, "用户 ID 不能为空");
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.getAccessTokenExpirationMinutes(), ChronoUnit.MINUTES);

        return Jwts.builder()
                .issuer(properties.getIssuer())
                .subject(userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_EMAIL, email)
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 解析 access token，并返回当前登录用户的关键信息。
     *
     * <p>后续 JWT 拦截器会用这个方法：
     * 先从请求头里拿到 token，再解析出 userId，最后把 userId 放到当前请求上下文里。</p>
     *
     * @param token JWT 字符串，可以是纯 token，也可以带 Bearer 前缀
     * @return token 中携带的用户信息
     */
    public JwtUserInfo parseAccessToken(String token) {
        Claims claims = parseClaims(token);
        String userIdText = String.valueOf(claims.get(CLAIM_USER_ID));
        String email = claims.get(CLAIM_EMAIL, String.class);

        try {
            return new JwtUserInfo(UUID.fromString(userIdText), email);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED, "token 中的用户 ID 格式不正确");
        }
    }

    /**
     * 解析 token 里的原始 claims。
     *
     * <p>claims 可以理解为 token 里的业务数据，例如 userId、email。
     * 这个方法保留给后续扩展使用，比如以后 token 里需要加入角色、客户端类型等字段。</p>
     *
     * @param token JWT 字符串，可以是纯 token，也可以带 Bearer 前缀
     * @return JJWT 解析出来的 claims
     */
    public Claims parseClaims(String token) {
        String rawToken = removeBearerPrefix(token);
        if (!StringUtils.hasText(rawToken)) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED, "token 不能为空");
        }

        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(properties.getIssuer())
                    .build()
                    .parseSignedClaims(rawToken)
                    .getPayload();
        } catch (ExpiredJwtException ex) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED, "登录已过期，请重新登录");
        } catch (JwtException | IllegalArgumentException ex) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED, "token 无效，请重新登录");
        }
    }

    /**
     * 从 Authorization 请求头里取出真正的 token。
     *
     * <p>如果前端传的是 {@code Bearer xxx}，这里会返回 {@code xxx}。
     * 如果前端已经直接传了纯 token，也会原样返回，方便本地测试。</p>
     */
    public String removeBearerPrefix(String token) {
        if (!StringUtils.hasText(token)) {
            return token;
        }
        if (token.startsWith(BEARER_PREFIX)) {
            return token.substring(BEARER_PREFIX.length()).trim();
        }
        return token.trim();
    }

    private SecretKey buildSigningKey(String secret) {
        if (!StringUtils.hasText(secret)) {
            throw new IllegalStateException("JWT secret 不能为空，请检查 linkup.jwt.secret 配置");
        }

        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException("JWT secret 至少需要 32 字节，请使用更长、更随机的密钥");
        }

        return Keys.hmacShaKeyFor(secretBytes);
    }

    /**
     * token 解析后的当前登录用户信息。
     *
     * <p>这里用 record 是因为它只是一个简单的数据载体：
     * 有 userId 和 email，不需要额外业务方法。</p>
     */
    public record JwtUserInfo(UUID userId, String email) {
    }
}
