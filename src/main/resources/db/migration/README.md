# Flyway 数据库迁移脚本

所有 PostgreSQL schema 变更必须通过此目录下的版本化 SQL 脚本进行，**禁止直接在 DB 上手工改表**。

## 文件命名规范

`V<版本号>__<描述>.sql` —— 版本号必须严格递增，不允许跳号；描述用下划线连接，不要空格。

示例：
- `V1__init.sql` —— 初始化全表
- `V2__add_user_avatar.sql` —— 给 app_user 加 avatar_url 字段
- `V3__create_voucher_index.sql` —— 给 voucher 加复合索引

## 执行时机

Spring Boot 启动时自动执行未应用的迁移；执行记录写入 `flyway_schema_history` 表，**不要手工改这张表**。

## 注意事项

1. **已应用的脚本不允许修改**（Flyway 校验 checksum），如需变更请新增 V(N+1) 脚本。
2. PostgreSQL enum 增加值用 `ALTER TYPE ... ADD VALUE`；删除值需要 drop & recreate，谨慎。
3. 新表的 `created_at` / `updated_at` 字段统一 `timestamp with time zone default now()`，配合 MyBatis-Plus 的 `AuditMetaObjectHandler`。
