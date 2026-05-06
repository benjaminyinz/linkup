package com.linkup.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j / OpenAPI 接口文档配置。
 *
 * <p>Knife4j 是 Swagger / OpenAPI 的增强 UI。
 * 简单理解：</p>
 *
 * <ul>
 *     <li>OpenAPI：描述后端有哪些接口、参数是什么、返回什么。</li>
 *     <li>Knife4j：把 OpenAPI 描述渲染成更好用的接口文档页面。</li>
 * </ul>
 *
 * <p>后端启动后，通常可以访问 {@code /doc.html} 查看接口文档。
 * 当前配置先按 MVP 阶段只扫描 {@code /api/**} 接口。</p>
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "Authorization";

    /**
     * 接口文档基础信息。
     *
     * <p>这里配置项目名称、版本，以及 JWT 的 Authorization 请求头。
     * 后续 Knife4j 页面里调试需要登录的接口时，就可以统一填 Bearer token。</p>
     *
     * <p>注意：这里只定义 JWT 安全方案，不设置成全局强制。
     * 因为注册、登录接口本身不需要 token；后续真正需要登录的接口，再单独标注安全要求。</p>
     */
    @Bean
    public OpenAPI linkupOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LinkUp API")
                        .version("1.0.0")
                        .description("LinkUp MVP 后端接口文档"))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    /**
     * API 分组。
     *
     * <p>目前只建一个 App API 分组，扫描 /api/**。
     * 等后台管理端、商户端接口出现后，可以再拆成 admin-api、merchant-api 等分组。</p>
     */
    @Bean
    public GroupedOpenApi appApi() {
        return GroupedOpenApi.builder()
                .group("app-api")
                .pathsToMatch("/api/**")
                .build();
    }
}
