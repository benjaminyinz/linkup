package com.linkup.account.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密配置。
 *
 * <p>真实项目不能把用户密码明文存进数据库。
 * 注册时要把明文密码转换成不可逆的哈希值；登录时再用同样的算法校验。</p>
 *
 * <p>这里使用 BCrypt，它会自动加盐，同一个密码每次生成的哈希也不一样，
 * 比普通 MD5 / SHA 字符串更适合保存用户密码。</p>
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * BCrypt 密码编码器。
     *
     * <p>strength 表示计算强度，数字越大越安全，但 CPU 消耗也越高。
     * 10 是生产项目里常见的起步值，适合 MVP 阶段。</p>
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
