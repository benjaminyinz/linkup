package com.linkup.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.linkup.infrastructure.cache.CacheService;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 基础设施冒烟测试：验证 Phase 2 的 Flyway / 数据源 / Redis 三件套能在测试环境跑通。
 *
 * <p>不验证业务逻辑——业务还没重写。重点是：
 * <ul>
 *   <li>Spring 上下文能起来</li>
 *   <li>PostgreSQL 容器连得上、Flyway 历史表存在</li>
 *   <li>Redis 容器连得上、CacheService 读写正常</li>
 * </ul></p>
 */
class InfrastructureSmokeTest extends BaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CacheService cacheService;

    @Test
    void flyway_history_table_exists() {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from information_schema.tables where table_name = 'flyway_schema_history'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void redis_round_trip() {
        String key = "linkup:test:smoke:" + UUID.randomUUID();
        cacheService.put(key, "hello", Duration.ofSeconds(30));
        assertThat(cacheService.get(key, String.class)).contains("hello");
        cacheService.evict(key);
        assertThat(cacheService.exists(key)).isFalse();
    }
}
