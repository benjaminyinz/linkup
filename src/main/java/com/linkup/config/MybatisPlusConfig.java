package com.linkup.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 乐观锁：entity 的 @Version 字段在 update 时自动 +1，并在 where 子句中带上原值，
        // 防止活动名额、钱包余额等高并发场景出现"丢失更新"。注册顺序需在分页插件之前。
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // PostgreSQL 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.getDbType("postgresql")));

        // 防止误执行全表 UPDATE / DELETE
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }
}
