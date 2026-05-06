-- ============================================================
-- LinkUp 完整数据库初始化脚本
-- 执行顺序：Step 1 用 postgres 超级用户执行，Step 2 之后用 linkup 用户执行
-- ============================================================


-- ============================================================
-- Step 1：创建数据库和用户（用 postgres 超级用户执行）
-- ============================================================

CREATE USER linkup WITH PASSWORD 'your_password_here';

CREATE DATABASE linkup_db
    OWNER = linkup
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TEMPLATE = template0;

-- 执行完后切换到 linkup_db：\c linkup_db


-- ============================================================
-- Step 2：启用扩展（在 linkup_db 下执行）
-- ============================================================

-- pgcrypto 提供 gen_random_uuid()，备用；主要用应用层 UUID v7
CREATE EXTENSION IF NOT EXISTS "pgcrypto";


-- ============================================================
-- Step 3：创建 Schema（逻辑分组，非必须，默认 public 也可以）
-- ============================================================

-- 使用默认 public schema，无需额外创建
-- 如需多租户或模块隔离，可改为 CREATE SCHEMA linkup;


-- ============================================================
-- Step 4：ENUM 类型定义
-- ============================================================

CREATE TYPE auth_provider           AS ENUM ('email');
-- 当前只支持邮箱登录；后续扩展 Apple/Google 时在此添加新值

CREATE TYPE gender_type             AS ENUM ('male', 'female');
-- male   = 男
-- female = 女

CREATE TYPE activity_fee_type       AS ENUM ('free', 'split', 'fixed');
-- free   = 免费
-- split  = AA 制（到场各自付）
-- fixed  = 固定收费（host 定价，平台代收）

CREATE TYPE activity_status         AS ENUM ('open', 'full', 'completed', 'cancelled');

CREATE TYPE participant_status      AS ENUM ('joined', 'cancelled', 'completed');
-- 无候补功能，满员后直接拒绝加入

CREATE TYPE payment_status          AS ENUM ('pending', 'paid', 'refunded', 'failed');

CREATE TYPE payout_status           AS ENUM ('pending', 'processing', 'completed');

CREATE TYPE deal_segment            AS ENUM ('food', 'entertainment', 'sports', 'beauty');

CREATE TYPE deal_status             AS ENUM ('active', 'ended');
-- active = 售卖中
-- ended  = 已结束（过期或手动下架统一用此状态）

CREATE TYPE redeem_status           AS ENUM ('unused', 'used', 'expired');

CREATE TYPE refund_status           AS ENUM ('none', 'requested', 'processing', 'completed');

CREATE TYPE wallet_tx_type          AS ENUM ('activity_income', 'refund', 'withdrawal');
-- activity_income = host 收到参与者付款
-- refund          = 退款回到用户
-- withdrawal      = 用户提现到银行

CREATE TYPE wallet_tx_status        AS ENUM ('pending', 'completed', 'failed');

CREATE TYPE wallet_ref_type         AS ENUM ('activity_participant', 'app_order');

CREATE TYPE voucher_source          AS ENUM ('invite_reward', 'activity', 'platform');
-- invite_reward = 邀请好友奖励
-- activity      = 活动相关赠券
-- platform      = 平台发放

CREATE TYPE voucher_status          AS ENUM ('unused', 'used', 'expired');

CREATE TYPE invite_status           AS ENUM ('pending', 'rewarded');
-- pending  = 好友已注册但未完成首单
-- rewarded = 好友完成首单，双方已得奖励


-- ============================================================
-- Step 5：建表
-- 顺序：被引用的表在前，引用方在后
-- ============================================================

-- ------------------------------------------------------------
-- 用户模块
-- ------------------------------------------------------------

-- 用户主表
-- app_user 避免与 PostgreSQL 保留字 user 冲突
CREATE TABLE app_user (
    id                    UUID         PRIMARY KEY,                  -- UUID v7，应用层生成
    nickname              VARCHAR(50)  NOT NULL,
    avatar_url            VARCHAR(500),                              -- 头像，S3 URL；未设置则前端用首字母生成
    phone                 VARCHAR(20),                               -- 手机号；纯 OAuth 用户可为空
    email                 VARCHAR(100),
    signature             VARCHAR(100),                              -- 个性签名
    gender                gender_type,                               -- 性别；可不填
    level                 SMALLINT     NOT NULL DEFAULT 1,           -- 当前等级 1~4
    level_points          INT          NOT NULL DEFAULT 0,           -- 成长值，等级 2→3→4 依赖此字段
    joined_activity_count INT          NOT NULL DEFAULT 0,           -- 累计参与找搭子次数，用于 1→2 级判断
    invite_code           VARCHAR(20)  NOT NULL UNIQUE,              -- 邀请码，如 YOUJU-BEN888
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
-- 等级规则（业务层执行）：
--   Lv.1 → Lv.2：joined_activity_count >= 5
--   Lv.2 → Lv.3：level_points >= 62
--   Lv.3 → Lv.4：level_points >= 88
-- 等级权限（业务层执行）：
--   Lv.1：不可发起活动
--   Lv.2：可发起免费 / AA 制活动
--   Lv.3+：可发起固定收费活动

-- 登录方式表
-- 一个用户可同时绑定多种登录方式（Apple/Google/手机号）
-- 登录方式表
-- 当前只支持邮箱登录；provider_user_id 存邮箱地址，password_hash 存 bcrypt 哈希
CREATE TABLE user_auth (
    id                UUID          PRIMARY KEY,                     -- UUID v7，应用层生成
    user_id           UUID          NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    provider          auth_provider NOT NULL DEFAULT 'email',
    provider_user_id  VARCHAR(200)  NOT NULL,                        -- 邮箱地址
    password_hash     VARCHAR(200),                                  -- bcrypt 哈希；OAuth 登录时为空
    created_at        TIMESTAMPTZ   NOT NULL DEFAULT NOW(),

    UNIQUE (provider, provider_user_id)                              -- 同一登录方式不能绑多个账号
);
CREATE INDEX idx_user_auth_user_id ON user_auth(user_id);

-- 绑定银行账户（NZ 本地格式 XX-XXXX-XXXXXXX-XX）
-- 用于 host 提现，每个用户只允许绑定一个账户
CREATE TABLE bank_account (
    id              UUID         PRIMARY KEY,                        -- UUID v7，应用层生成
    user_id         UUID         NOT NULL REFERENCES app_user(id) ON DELETE CASCADE UNIQUE,
    account_name    VARCHAR(100) NOT NULL,                           -- 账户持有人姓名
    account_number  VARCHAR(30)  NOT NULL,                           -- NZ 格式：XX-XXXX-XXXXXXX-XX
    bank_name       VARCHAR(100),                                    -- 银行名称，可选
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
-- user_id 已是 UNIQUE，PostgreSQL 自动建索引，无需手动添加

-- 优惠券表
-- RESTRICT：用户有券记录时不允许硬删除用户（业务上应使用软删除）
CREATE TABLE voucher (
    id          UUID           PRIMARY KEY,                          -- UUID v7，应用层生成
    user_id     UUID           NOT NULL REFERENCES app_user(id) ON DELETE RESTRICT,
    amount      INT            NOT NULL,                             -- 面额（分），如 500 = $5
    rule        VARCHAR(100)   NOT NULL,                             -- 使用门槛，如"满 $50 可用"
    title       VARCHAR(100)   NOT NULL,
    subtitle    VARCHAR(200),
    source      voucher_source NOT NULL,
    status      voucher_status NOT NULL DEFAULT 'unused',
    expires_at  TIMESTAMPTZ    NOT NULL,
    used_at     TIMESTAMPTZ,                                         -- 使用时间
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_voucher_user_id ON voucher(user_id);

-- 邀请记录
-- 好友完成首单后双方各得奖励券
CREATE TABLE invite_record (
    id                      UUID          PRIMARY KEY,               -- UUID v7，应用层生成
    inviter_id              UUID          NOT NULL REFERENCES app_user(id),
    invitee_id              UUID          NOT NULL REFERENCES app_user(id),
    status                  invite_status NOT NULL DEFAULT 'pending',
    invitee_first_order_id  UUID,                                    -- 触发奖励的首单 app_order.id
    reward_amount           INT           NOT NULL DEFAULT 500,      -- 奖励面额（分），默认 $5
    rewarded_at             TIMESTAMPTZ,
    created_at              TIMESTAMPTZ   NOT NULL DEFAULT NOW(),

    UNIQUE (inviter_id, invitee_id)                                  -- 同一对关系只记录一次
);
CREATE INDEX idx_invite_record_inviter_id ON invite_record(inviter_id);
CREATE INDEX idx_invite_record_invitee_id ON invite_record(invitee_id);


-- ------------------------------------------------------------
-- 找搭子模块
-- ------------------------------------------------------------

-- 活动主表
CREATE TABLE activity (
    id                        UUID              PRIMARY KEY,         -- UUID v7，应用层生成
    host_id                   UUID              NOT NULL REFERENCES app_user(id),
    title                     VARCHAR(100)      NOT NULL,
    emoji                     VARCHAR(10)       NOT NULL,
    description               TEXT,
    category_label            VARCHAR(50)       NOT NULL,            -- 羽毛球 / 火锅 / 电影 等
    style_key                 VARCHAR(50)       NOT NULL,            -- 前端色板标识
    event_date                DATE              NOT NULL,
    start_time                TIME              NOT NULL,            -- NZ 本地时间（无时区）
    location_name             VARCHAR(100)      NOT NULL,            -- 地点简称，卡片展示
    location_detail           VARCHAR(200)      NOT NULL,            -- 完整地址，详情页 + 地图定位
    latitude                  DECIMAL(9,6),                          -- 纬度，geocoding 后写入；可为空
    longitude                 DECIMAL(9,6),                          -- 经度，geocoding 后写入；可为空
    max_participants          INT               NOT NULL CHECK (max_participants BETWEEN 2 AND 20),
    current_participants      INT               NOT NULL DEFAULT 0,  -- 缓存计数，避免每次 COUNT
    fee_type                  activity_fee_type NOT NULL DEFAULT 'free',
    price                     INT               NOT NULL DEFAULT 0,  -- 分；fee_type=fixed 时有意义
    fee_detail                VARCHAR(200),                          -- 费用说明，如"AA 制，人均约 $15"
    cta_text                  VARCHAR(20)       NOT NULL DEFAULT '加入',
    status                    activity_status   NOT NULL DEFAULT 'open',
    firebase_conversation_id  VARCHAR(100),                          -- 消息模块接入后填入，现阶段留空
    created_at                TIMESTAMPTZ       NOT NULL DEFAULT NOW(),
    updated_at                TIMESTAMPTZ       NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_activity_host_id    ON activity(host_id);
CREATE INDEX idx_activity_event_date ON activity(event_date);       -- 按日期查询活动列表
CREATE INDEX idx_activity_status     ON activity(status);

-- 活动参与记录
-- RESTRICT：有参与记录时不允许硬删除活动
CREATE TABLE activity_participant (
    id                 UUID               PRIMARY KEY,               -- UUID v7，应用层生成
    activity_id        UUID               NOT NULL REFERENCES activity(id) ON DELETE RESTRICT,
    user_id            UUID               NOT NULL REFERENCES app_user(id),
    status             participant_status NOT NULL DEFAULT 'joined',
    registration_code  VARCHAR(20)        UNIQUE,                    -- 报名码，付费活动生成，现场核验用
    payment_reference  VARCHAR(200),                                 -- 支付网关 ID（如 Stripe payment_intent_id）
    payment_status     payment_status     NOT NULL DEFAULT 'pending',
    amount_paid        INT                NOT NULL DEFAULT 0,        -- 实付金额（分）；免费/AA 制为 0
    joined_at          TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
    cancelled_at       TIMESTAMPTZ,

    UNIQUE (activity_id, user_id)                                    -- 防重复加入
);
CREATE INDEX idx_activity_participant_activity_id ON activity_participant(activity_id);
CREATE INDEX idx_activity_participant_user_id     ON activity_participant(user_id);

-- Host 收款记录（每场活动一条）
-- 平台代收后人工核对再打款
CREATE TABLE activity_payout (
    id              UUID          PRIMARY KEY,                       -- UUID v7，应用层生成
    activity_id     UUID          NOT NULL REFERENCES activity(id) UNIQUE,
    host_id         UUID          NOT NULL REFERENCES app_user(id),
    total_amount    INT           NOT NULL,                          -- 平台收到的总额（分）
    platform_fee    INT           NOT NULL DEFAULT 0,               -- 平台手续费（分），暂定 0
    payout_amount   INT           NOT NULL,                          -- 实转 host = total - platform_fee
    status          payout_status NOT NULL DEFAULT 'pending',
    note            TEXT,                                            -- 人工操作备注
    processed_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_activity_payout_host_id ON activity_payout(host_id);

-- 活动图片（sort_order=0 为封面图）
CREATE TABLE activity_image (
    id           UUID         PRIMARY KEY,                           -- UUID v7，应用层生成
    activity_id  UUID         NOT NULL REFERENCES activity(id) ON DELETE CASCADE,
    s3_url       VARCHAR(500) NOT NULL,
    sort_order   INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_activity_image_activity_id ON activity_image(activity_id);


-- ------------------------------------------------------------
-- 团购模块
-- ------------------------------------------------------------

-- 商户表
CREATE TABLE merchant (
    id          UUID         PRIMARY KEY,                            -- UUID v7，应用层生成
    name        VARCHAR(100) NOT NULL,
    address     VARCHAR(200) NOT NULL,
    phone       VARCHAR(20)  NOT NULL,
    hours       VARCHAR(100) NOT NULL,                               -- 如"周一至周日 11:30–22:00"
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- 团购商品
CREATE TABLE deal (
    id              UUID         PRIMARY KEY,                        -- UUID v7，应用层生成
    merchant_id     UUID         NOT NULL REFERENCES merchant(id),
    segment         deal_segment NOT NULL,
    style_key       VARCHAR(50)  NOT NULL,                           -- 前端色板标识
    title           VARCHAR(100) NOT NULL,
    short_title     VARCHAR(50)  NOT NULL,                           -- 列表卡片用简短标题
    subtitle        VARCHAR(200) NOT NULL,                           -- 如"蜀味居 · CBD"
    emoji           VARCHAR(10)  NOT NULL,
    badge           VARCHAR(20),                                     -- 角标，如"限时"/"热卖"
    price           INT          NOT NULL,                           -- 现价（分）
    original_price  INT          NOT NULL,                           -- 原价（分）
    discount_text   VARCHAR(20)  NOT NULL,                           -- 如"7折"
    description     TEXT         NOT NULL,
    validity_days   INT          NOT NULL,                           -- 购买后有效天数
    includes        VARCHAR(200) NOT NULL,                           -- 如"2人套餐 · 饮料 · 甜品"
    refund_policy   TEXT         NOT NULL,
    location_area   VARCHAR(50)  NOT NULL,                           -- 如"CBD"/"Parnell"
    sold_count      INT          NOT NULL DEFAULT 0,                 -- 已售量缓存，下单成功时 +1
    status          deal_status  NOT NULL DEFAULT 'active',
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_deal_merchant_id ON deal(merchant_id);
CREATE INDEX idx_deal_segment     ON deal(segment);
CREATE INDEX idx_deal_status      ON deal(status);

-- 使用须知条目（对应前端 termsOfUse 数组）
CREATE TABLE deal_terms (
    id          UUID         PRIMARY KEY,                            -- UUID v7，应用层生成
    deal_id     UUID         NOT NULL REFERENCES deal(id) ON DELETE CASCADE,
    content     VARCHAR(200) NOT NULL,
    sort_order  INT          NOT NULL DEFAULT 0
);
CREATE INDEX idx_deal_terms_deal_id ON deal_terms(deal_id);

-- 订单表（每人每 deal 只能下一单，一单一码）
-- app_order 避免与 PostgreSQL 保留字 order 冲突
CREATE TABLE app_order (
    id                 UUID           PRIMARY KEY,                   -- UUID v7，应用层生成
    user_id            UUID           NOT NULL REFERENCES app_user(id),
    deal_id            UUID           NOT NULL REFERENCES deal(id),
    order_no           VARCHAR(30)    NOT NULL UNIQUE,               -- 展示用订单号，如 LK20260422001
    payment_no         VARCHAR(100),                                 -- 支付网关流水号
    amount_paid        INT            NOT NULL,                      -- 实付金额（分）
    payment_reference  VARCHAR(200)   NOT NULL,                      -- 支付网关 ID
    payment_status     payment_status NOT NULL DEFAULT 'pending',
    redeem_code        VARCHAR(10)    NOT NULL UNIQUE,               -- 核销码，全局唯一
    redeem_status      redeem_status  NOT NULL DEFAULT 'unused',
    redeemed_at        TIMESTAMPTZ,
    refund_status      refund_status  NOT NULL DEFAULT 'none',
    refund_reason      VARCHAR(500),
    after_sale_note    VARCHAR(500),                                 -- 售后处理说明，客服填写
    usage_window       VARCHAR(100),                                 -- 如"周末及节假日通用"
    usage_rule         VARCHAR(200),                                 -- 如"需提前1天预约"
    package_snapshot   JSONB,                                        -- 购买时固化套餐快照，防改价影响历史订单
    expires_at         TIMESTAMPTZ    NOT NULL,
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),

    UNIQUE (user_id, deal_id)                                        -- 每人每 deal 只能买一单
);
CREATE INDEX idx_order_user_id ON app_order(user_id);
CREATE INDEX idx_order_deal_id ON app_order(deal_id);
-- package_snapshot 结构示例：
-- {
--   "title": "双人套餐",
--   "spec": "含饮料",
--   "partySize": "2人",
--   "items": [
--     { "title": "烤肉拼盘", "quantity": "1份" },
--     { "title": "软饮", "quantity": "2杯", "note": "可换果汁" }
--   ]
-- }


-- ------------------------------------------------------------
-- 公共模块
-- ------------------------------------------------------------

-- 钱包流水（amount 正=收入，负=提现）
CREATE TABLE wallet_transaction (
    id              UUID             PRIMARY KEY,                    -- UUID v7，应用层生成
    user_id         UUID             NOT NULL REFERENCES app_user(id),
    type            wallet_tx_type   NOT NULL,
    amount          INT              NOT NULL,                       -- 分；正=收入，负=提现
    reference_id    UUID,                                            -- 关联 activity_participant.id 或 app_order.id
    reference_type  wallet_ref_type,                                 -- 说明 reference_id 指向哪张表
    status          wallet_tx_status NOT NULL DEFAULT 'pending',
    note            VARCHAR(200),                                    -- 如提现银行账号末四位
    created_at      TIMESTAMPTZ      NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_wallet_transaction_user_id ON wallet_transaction(user_id);
-- 余额计算规则（业务层聚合）：
--   可提现余额 = SUM(amount) WHERE status='completed' AND type IN ('activity_income','refund')
--               MINUS SUM(ABS(amount)) WHERE status IN ('pending','completed') AND type='withdrawal'
--   处理中金额 = SUM(ABS(amount)) WHERE status='pending' AND type='withdrawal'
--   预计到账   = SUM(amount) WHERE status='pending' AND type='activity_income'
--   累计收入   = SUM(amount) WHERE status='completed' AND type='activity_income'


-- ============================================================
-- Step 6：授权
-- ============================================================

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO linkup;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO linkup;
GRANT ALL PRIVILEGES ON ALL TYPES IN SCHEMA public TO linkup;


-- ============================================================
-- Spring Boot application.properties 配置参考
-- ============================================================
-- spring.datasource.url=jdbc:postgresql://localhost:5432/linkup_db
-- spring.datasource.username=linkup
-- spring.datasource.password=your_password_here
-- spring.datasource.driver-class-name=org.postgresql.Driver
-- spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
-- spring.jpa.hibernate.ddl-auto=validate
-- spring.jpa.properties.hibernate.jdbc.time_zone=Pacific/Auckland
