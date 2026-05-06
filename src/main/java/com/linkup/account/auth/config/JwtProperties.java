package com.linkup.account.auth.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * JWT 配置项。
 *
 * <p>这个类专门负责读取配置文件里的 {@code linkup.jwt.*} 配置。
 * 真实项目里不要把 JWT 密钥、过期时间写死在工具类里，因为不同环境的配置通常不一样：</p>
 *
 * <ul>
 *     <li>开发环境：可以使用本地默认密钥，方便启动项目。</li>
 *     <li>生产环境：必须通过环境变量传入真正的密钥，不能提交到代码仓库。</li>
 * </ul>
 *
 * <p>Controller / Service 不需要直接读取 yml，它们只需要使用 JwtTokenProvider。
 * 这样以后修改 JWT 配置来源时，不会影响业务代码。</p>
 */
@Data
@Validated
@Component
@ConfigurationProperties(prefix = "linkup.jwt")
public class JwtProperties {

    /**
     * JWT 签名密钥。
     *
     * <p>签名密钥用于证明 token 是后端签发的。
     * 如果别人拿到了这个密钥，就可以伪造 token，所以生产环境必须放在环境变量里。</p>
     *
     * <p>当前使用 HS256 算法，密钥至少需要 32 字节。
     * 英文、数字、符号通常 1 个字符约等于 1 字节，所以建议至少 32 个字符。</p>
     */
    @NotBlank(message = "JWT 密钥不能为空")
    private String secret;

    /**
     * JWT 签发方。
     *
     * <p>签发方可以理解为“这个 token 是谁发的”。
     * 项目里统一写成 linkup，后续如果有多个服务或多个端，可以靠它辅助排查问题。</p>
     */
    @NotBlank(message = "JWT 签发方不能为空")
    private String issuer = "linkup";

    /**
     * access token 有效期，单位：分钟。
     *
     * <p>MVP 阶段先用一个较长的 access token，减少 refresh token 带来的复杂度。
     * 后续如果要做更严格的安全策略，再增加 refresh token、黑名单、多端登录控制。</p>
     */
    @Min(value = 1, message = "JWT 有效期必须大于 0 分钟")
    private long accessTokenExpirationMinutes = 43200;
}
