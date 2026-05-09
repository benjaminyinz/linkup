package com.linkup.infrastructure.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.time.OffsetDateTime;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * 审计字段自动填充。
 *
 * <p>在 Entity 上用 {@code @TableField(fill = FieldFill.INSERT)} 标记的字段，
 * 会在插入前调用 {@link #insertFill(MetaObject)}；
 * 用 {@code FieldFill.INSERT_UPDATE} 标记的字段，更新前还会调用 {@link #updateFill(MetaObject)}。</p>
 *
 * <p>这里统一把 created_at / updated_at / joined_at 这类审计列填上当前时间。
 * {@link #strictInsertFill}/{@link #strictUpdateFill} 在目标字段不存在时静默跳过，
 * 所以同一个 Handler 可以服务所有实体。</p>
 *
 * <p>数据库列虽然有 {@code DEFAULT NOW()}，但 MyBatis-Plus 会把字段为 null 的列
 * 也写进 INSERT 语句（值传 NULL），从而绕过 DB 默认值并触发 NOT NULL 报错；
 * 所以这一层兜底必须存在。</p>
 */
@Component
public class AuditMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        OffsetDateTime now = OffsetDateTime.now();
        strictInsertFill(metaObject, "createdAt", OffsetDateTime.class, now);
        strictInsertFill(metaObject, "updatedAt", OffsetDateTime.class, now);
        strictInsertFill(metaObject, "joinedAt", OffsetDateTime.class, now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "updatedAt", OffsetDateTime.class, OffsetDateTime.now());
    }
}
