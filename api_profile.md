# LinkUp Profile 模块 API 文档

所有接口需携带 `Authorization: Bearer <token>` 请求头。  
统一响应格式：`{ "code": 0, "message": "ok", "data": { ... } }`  
`code = 0` 表示成功，其他值表示业务错误。

---

## 一、Auth（鉴权）

Profile 所有接口依赖登录态，Auth 是前置条件。

### 1.1 注册

`POST /api/auth/register`

请求体：
```json
{
  "email": "ben@example.com",
  "password": "Abc12345",
  "nickname": "Benjamin"
}
```

响应 data：
```json
{
  "token": "eyJhbGci...",
  "user": {
    "id": "uuid",
    "nickname": "Benjamin",
    "email": "ben@example.com",
    "level": 1,
    "inviteCode": "YOUJU-BEN888"
  }
}
```

### 1.2 登录

`POST /api/auth/login`

请求体：
```json
{
  "email": "ben@example.com",
  "password": "Abc12345"
}
```

响应 data：同注册。

### 1.3 退出登录

`POST /api/auth/logout`

请求体：无  
响应 data：null

---

## 二、用户信息

### 2.1 获取当前用户

`GET /api/user/me`

响应 data：
```json
{
  "id": "uuid",
  "nickname": "Benjamin",
  "avatarUrl": "https://s3.../avatar.jpg",
  "email": "ben@example.com",
  "gender": "male",
  "signature": "出门要有个伴儿",
  "level": 2,
  "levelPoints": 30,
  "joinedActivityCount": 7,
  "inviteCode": "YOUJU-BEN888",
  "stats": {
    "joinedCount": 12,
    "metPeopleCount": 28,
    "savedAmount": 0
  }
}
```

### 2.2 更新个人资料

`PUT /api/user/me`

请求体（所有字段可选，只传需要改的）：
```json
{
  "nickname": "Ben",
  "gender": "male",
  "signature": "Auckland 里，总有人和你同行"
}
```

响应 data：更新后的用户对象，结构同 2.1。

---

## 三、等级中心

### 3.1 获取等级信息

`GET /api/user/level`

响应 data：
```json
{
  "currentLevel": 2,
  "levelPoints": 30,
  "joinedActivityCount": 7,
  "tiers": [
    {
      "id": 1,
      "name": "新手",
      "tagline": "先玩局，再发局",
      "targetPoints": 0,
      "privileges": ["新人欢迎券", "可报名普通活动"]
    },
    {
      "id": 2,
      "name": "玩家",
      "tagline": "已解锁：发布免费的局",
      "targetPoints": 62,
      "privileges": ["每月福利券2张", "热门活动优先提醒"]
    },
    {
      "id": 3,
      "name": "达人",
      "tagline": "已解锁：发布付费的局",
      "targetPoints": 88,
      "privileges": ["高阶福利券包", "活动轻曝光加成"]
    },
    {
      "id": 4,
      "name": "局座",
      "tagline": "发局享优先曝光，顶级局座",
      "targetPoints": 100,
      "privileges": ["主理人专属福利包", "活动优先曝光"]
    }
  ]
}
```

---

## 四、近期动态

### 4.1 获取近期参与/发起的活动

`GET /api/mobile/profile/recent`（已有，复用）

响应 data：数组，每项：
```json
{
  "activityId": "uuid",
  "title": "周末羽毛球",
  "subtitle": "2人 · 免费",
  "price": 0,
  "emoji": "🏸",
  "styleKey": "badminton",
  "status": "已完成",
  "statusStyleKey": "green",
  "category": "joined"
}
```

---

## 五、订单

### 5.1 获取订单列表

`GET /api/orders?status=all`

Query 参数：
- `status`: `all` / `pending_use` / `completed` / `after_sale`（默认 `all`）

响应 data：数组，每项：
```json
{
  "id": "uuid",
  "orderNo": "LK20260422001",
  "dealEmoji": "🍲",
  "dealTitle": "蜀味居双人套餐",
  "merchantName": "蜀味居川菜馆",
  "merchantAddress": "123 Queen St, CBD",
  "merchantHours": "11:30–22:00",
  "merchantPhone": "09-123-4567",
  "packageSummary": "双人套餐 · 含饮料",
  "packageSnapshot": {
    "title": "双人套餐",
    "spec": "含饮料",
    "partySize": "2人",
    "items": [
      { "title": "烤肉拼盘", "quantity": "1份" },
      { "title": "软饮", "quantity": "2杯", "note": "可换果汁" }
    ]
  },
  "usageWindow": "周末及节假日通用",
  "usageRule": "需提前1天预约",
  "redeemCode": "AB1234",
  "orderNo": "LK20260422001",
  "paymentNo": "pi_stripe_xxx",
  "amountPaid": 6800,
  "purchasedAt": "2026-04-22T10:30:00+12:00",
  "usedAt": null,
  "expiresAt": "2026-07-22T23:59:59+12:00",
  "paymentStatus": "paid",
  "redeemStatus": "unused",
  "refundStatus": "none",
  "afterSaleNote": null,
  "status": "pending_use"
}
```

### 5.2 获取订单详情

`GET /api/orders/{orderId}`

响应 data：结构同 5.1 单项。

---

## 六、钱包

### 6.1 获取钱包汇总

`GET /api/wallet/summary`

响应 data：
```json
{
  "withdrawableAmount": 4500,
  "processingAmount": 1000,
  "expectedAmount": 3000,
  "totalIncomeAmount": 25000
}
```
（单位：分）

### 6.2 获取交易记录

`GET /api/wallet/transactions?category=cashflow`

Query 参数：
- `category`: `cashflow`（收支明细） / `withdrawal`（提现记录）

响应 data：数组，每项：
```json
{
  "id": "uuid",
  "title": "活动报名收入",
  "subtitle": "周末羽毛球 · 2026/04/20",
  "amount": 1500,
  "type": "activity_income",
  "status": "completed",
  "transactionId": "TXN20260420001",
  "transactionTime": "2026-04-20T18:00:00+12:00",
  "activityTitle": "周末羽毛球",
  "activityDate": "2026-04-20",
  "refundReason": null,
  "refundId": null
}
```

---

## 七、优惠券

### 7.1 获取优惠券列表

`GET /api/vouchers?status=unused`

Query 参数：
- `status`: `unused` / `used` / `expired`（默认 `unused`）

响应 data：数组，每项：
```json
{
  "id": "uuid",
  "amount": 500,
  "rule": "满 $50 可用",
  "title": "新人专享券",
  "subtitle": "限团购使用",
  "source": "invite_reward",
  "status": "unused",
  "expiresAt": "2026-07-01T23:59:59+12:00",
  "usedAt": null
}
```

---

## 八、银行账户

### 8.1 获取绑定账户

`GET /api/bank-account`

响应 data：
```json
{
  "id": "uuid",
  "accountName": "Benjamin Yin",
  "accountNumber": "12-3456-7891234-00",
  "bankName": "ANZ"
}
```
未绑定时 data 为 null。

### 8.2 绑定或更新账户

`POST /api/bank-account`

请求体：
```json
{
  "accountName": "Benjamin Yin",
  "accountNumber": "12-3456-7891234-00",
  "bankName": "ANZ"
}
```

响应 data：同 8.1。

### 8.3 移除账户

`DELETE /api/bank-account`

响应 data：null

---

## 九、邀请奖励

### 9.1 获取邀请汇总

`GET /api/invite/summary`

响应 data：
```json
{
  "inviteCode": "YOUJU-BEN888",
  "totalInvited": 3,
  "confirmedAmount": 1000,
  "pendingAmount": 500
}
```
（金额单位：分）

### 9.2 获取已邀好友列表

`GET /api/invite/friends`

响应 data：数组，每项：
```json
{
  "id": "uuid",
  "nickname": "Alice",
  "avatarUrl": null,
  "joinedAt": "2026-03-15T10:00:00+12:00",
  "rewardAmount": 500,
  "isRewarded": true
}
```

---

## 错误码

| code | 含义 |
|------|------|
| 0 | 成功 |
| 1001 | 未登录或 token 过期 |
| 1002 | 邮箱已注册 |
| 1003 | 邮箱或密码错误 |
| 1004 | 参数校验失败 |
| 2001 | 资源不存在 |
| 5000 | 服务器内部错误 |
