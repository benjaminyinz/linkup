package com.linkup.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.linkup.account.auth.support.JwtAuthInterceptor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Spring MVC 基础配置。
 *
 * <p>这里放 Web 层的全局配置，例如跨域、静态资源映射、拦截器注册入口、JSON 时间格式。
 * 后续新增 Controller、JWT 拦截器、接口文档时，都会经过这层配置。</p>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final String API_PATH_PATTERN = "/api/**";

    private static final String AUTH_REGISTER_PATH = "/api/auth/register";

    private static final String AUTH_LOGIN_PATH = "/api/auth/login";

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    private static final String TIME_PATTERN = "HH:mm:ss";

    private static final String TIME_ZONE = "Pacific/Auckland";

    private final JwtAuthInterceptor jwtAuthInterceptor;

    public WebMvcConfig(JwtAuthInterceptor jwtAuthInterceptor) {
        this.jwtAuthInterceptor = jwtAuthInterceptor;
    }

    /**
     * 配置跨域。
     *
     * <p>前端 App 或 H5 开发阶段通常会跑在本机不同端口，例如 localhost:3000。
     * 浏览器会把这种“不同域名/端口”的请求视为跨域请求，所以后端要明确允许。</p>
     *
     * <p>这里只开放 /api/**，避免把所有路径都暴露给跨域访问。</p>
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(
                        "http://localhost:*",
                        "http://127.0.0.1:*",
                        "http://[::1]:*"
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 注册拦截器。
     *
     * <p>JWT 拦截器会保护 /api/** 下的大部分接口。
     * 注册和登录接口必须放行，因为用户还没有 token 时也要能访问它们。</p>
     *
     * <p>Knife4j 文档入口 /doc.html、/v3/api-docs、/webjars/** 不属于 /api/**，
     * 所以不会被这里拦截。</p>
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns(API_PATH_PATTERN)
                .excludePathPatterns(
                        AUTH_REGISTER_PATH,
                        AUTH_LOGIN_PATH
                );
    }

    /**
     * 配置接口文档相关静态资源。
     *
     * <p>Knife4j 的页面入口通常是 /doc.html，页面内部还会加载 webjars 下的 JS/CSS。
     * 显式配置这些映射，可以避免后续访问接口文档时出现静态资源 404。</p>
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
    }

    /**
     * 统一 JSON 时间格式。
     *
     * <p>项目主要面向新西兰场景，所以时区使用 Pacific/Auckland。
     * 这样后续接口返回 Date、LocalDateTime、LocalDate、LocalTime 时，格式会更稳定。</p>
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_PATTERN);

        return builder -> builder
                .timeZone(TimeZone.getTimeZone(TIME_ZONE))
                .simpleDateFormat(DATE_TIME_PATTERN)
                .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter))
                .serializerByType(LocalDate.class, new LocalDateSerializer(dateFormatter))
                .serializerByType(LocalTime.class, new LocalTimeSerializer(timeFormatter))
                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter))
                .deserializerByType(LocalDate.class, new LocalDateDeserializer(dateFormatter))
                .deserializerByType(LocalTime.class, new LocalTimeDeserializer(timeFormatter));
    }
}
