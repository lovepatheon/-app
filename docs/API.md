# WordHarbor 前后端接口约定

版本：`v0.1`  
状态：前端与后端 MVP 已实现
本地基础地址：`http://localhost:8080/api`

## 1. 联调规则

- 请求和响应统一使用 `application/json; charset=UTF-8`。
- 除登录、注册、刷新令牌外，其余接口使用 `Authorization: Bearer <accessToken>`。
- 时间使用 ISO 8601，例如 `2026-07-13T20:30:00+08:00`。
- 数据库主键在接口中使用数字，前端 TypeScript 类型为 `number`。
- 分页页码从 `0` 开始。
- Spring Boot 已设置 `server.servlet.context-path=/api`，Controller 中不要再次写 `/api`。
- 前端默认 `VITE_USE_MOCK=true`；后端接口完成后改成 `false` 即可发出真实请求。

## 2. 通用响应

所有成功和业务失败响应采用同一外层结构：

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "requestId": "01J2Y8KX4ABCD1234"
}
```

字段说明：

| 字段 | 类型 | 说明 |
|---|---|---|
| `code` | integer | `0` 表示成功，非 `0` 为业务错误码 |
| `message` | string | 面向调用方的结果信息 |
| `data` | object/null | 实际业务数据 |
| `requestId` | string | 请求追踪编号，可选 |

分页数据：

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 1942,
  "totalPages": 98
}
```

建议 HTTP 状态码：

| 状态码 | 场景 |
|---|---|
| `200` | 查询或修改成功 |
| `201` | 注册、创建学习会话成功 |
| `400` | 参数格式错误 |
| `401` | 未登录、Token 过期 |
| `403` | 已登录但无权限 |
| `404` | 单词或资源不存在 |
| `409` | 用户名、邮箱重复 |
| `422` | 业务规则校验失败 |
| `500` | 未处理的服务器错误 |

## 3. 枚举

### 词库等级 `WordLevel`

```text
CET4 | CET6
```

### 掌握程度 `MasteryLevel`

```text
NEW | LEARNING | FAMILIAR | MASTERED
```

### 回忆评分 `RecallRating`

```text
AGAIN | HARD | GOOD | EASY
```

### 学习模式 `StudyMode`

```text
NEW | REVIEW
```

## 4. 身份认证

### 4.1 注册

`POST /auth/register`

请求：

```json
{
  "username": "student01",
  "nickname": "小词同学",
  "email": "student@example.com",
  "password": "plain-password-from-https"
}
```

响应 `data`：

```json
{
  "accessToken": "jwt-access-token",
  "expiresIn": 7200,
  "user": {
    "id": 10001,
    "username": "student01",
    "nickname": "小词同学",
    "email": "student@example.com",
    "avatarText": "词",
    "continuousDays": 0,
    "settings": {
      "dailyNewWords": 20,
      "dailyReviewLimit": 80,
      "preferredLevel": "BOTH",
      "reminderTime": "20:30",
      "soundEnabled": true
    }
  }
}
```

后端要求：密码只保存 BCrypt/Argon2 摘要，不保存或记录明文。

### 4.2 登录

`POST /auth/login`

```json
{
  "username": "student01",
  "password": "plain-password-from-https"
}
```

响应与注册接口相同。生产环境建议 Refresh Token 使用 `HttpOnly + Secure + SameSite` Cookie，不返回给 JavaScript。

### 4.3 刷新访问令牌

`POST /auth/refresh`

不需要 JSON 请求体，由浏览器携带 Refresh Token Cookie。

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "accessToken": "new-jwt-access-token",
    "expiresIn": 7200
  }
}
```

### 4.4 退出登录

`POST /auth/logout`

后端撤销刷新会话并清除 Cookie，`data` 返回 `null`。

## 5. 用户与设置

### 5.1 当前用户

`GET /users/me`

响应 `data` 使用注册响应中的 `user` 结构。

### 5.2 更新学习设置

`PUT /users/me/settings`

```json
{
  "dailyNewWords": 20,
  "dailyReviewLimit": 80,
  "preferredLevel": "BOTH",
  "reminderTime": "20:30",
  "soundEnabled": true
}
```

校验规则：

- `dailyNewWords`：5–100。
- `dailyReviewLimit`：20–300。
- `preferredLevel`：`CET4 | CET6 | BOTH`。
- `reminderTime`：`HH:mm`。

响应 `data` 返回保存后的完整设置。

## 6. 今日学习首页

### 6.1 首页概览

`GET /study/dashboard`

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "greeting": "把今天的 20 个新词，变成明天的熟词。",
    "learnedToday": 12,
    "newWordTarget": 20,
    "reviewedToday": 28,
    "reviewDue": 16,
    "masteredTotal": 486,
    "continuousDays": 12,
    "weeklyActivity": [
      { "date": "07-13", "label": "日", "count": 12, "target": 20 }
    ],
    "difficultWords": [
      {
        "id": 1,
        "word": "contemplate",
        "phonetic": "/ˈkɒntəmpleɪt/",
        "level": ["CET6"],
        "briefDefinition": "沉思；仔细考虑",
        "mastery": "LEARNING"
      }
    ]
  }
}
```

## 7. 单词库

### 7.1 分页查询单词

`GET /words`

查询参数：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| `keyword` | string | 否 | 空 | 英文单词或中文释义 |
| `level` | enum | 否 | `ALL` | `ALL/CET4/CET6` |
| `mastery` | enum | 否 | `ALL` | 掌握程度 |
| `page` | integer | 否 | `0` | 页码 |
| `size` | integer | 否 | `20` | 每页数量，最大 100 |

响应 `data`：

```json
{
  "content": [
    {
      "id": 1,
      "word": "contemplate",
      "phonetic": "/ˈkɒntəmpleɪt/",
      "level": ["CET6"],
      "briefDefinition": "沉思；仔细考虑",
      "mastery": "LEARNING",
      "nextReviewAt": "2026-07-14T20:30:00+08:00"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1942,
  "totalPages": 98
}
```

### 7.2 单词详情

`GET /words/{id}`

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "word": "contemplate",
    "phonetic": "/ˈkɒntəmpleɪt/",
    "pronunciationUrl": "/media/pronunciation/contemplate.mp3",
    "level": ["CET6"],
    "briefDefinition": "沉思；仔细考虑",
    "mastery": "LEARNING",
    "senses": [
      {
        "partOfSpeech": "v.",
        "definitionCn": "深思，仔细考虑",
        "definitionEn": "to think deeply about something"
      }
    ],
    "examples": [
      {
        "sentence": "She contemplated the problem from every angle.",
        "translation": "她从各个角度仔细考虑了这个问题。"
      }
    ],
    "collocations": ["contemplate doing sth.", "contemplate the future"],
    "usageNote": "后接动名词，不接不定式。",
    "memoryTip": "像在安静的庙宇里沉思。",
    "source": "数据集名称与许可证"
  }
}
```

`source` 必须保存真实来源和许可证，不得省略商业词典内容的授权信息。

## 8. 学习与复习

### 8.1 获取学习队列

`GET /study/queue?mode=NEW`

`mode` 可以是 `NEW` 或 `REVIEW`。

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "sessionId": "01J2Y9SESSION123",
    "mode": "NEW",
    "total": 20,
    "completed": 0,
    "words": []
  }
}
```

`words` 元素使用单词详情结构。队列由后端生成，同一会话内顺序应保持稳定。

### 8.2 提交一次回忆结果

`POST /study/answers`

```json
{
  "sessionId": "01J2Y9SESSION123",
  "wordId": 1,
  "rating": "GOOD",
  "responseTimeMs": 4200
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "nextReviewAt": "2026-07-17T20:30:00+08:00",
    "intervalDays": 4,
    "mastery": "FAMILIAR"
  }
}
```

约束：

- `(userId, sessionId, wordId)` 应支持幂等，避免网络重试产生重复记录。
- `nextReviewAt`、`intervalDays`、`mastery` 由后端算法计算，前端不决定复习计划。
- `responseTimeMs` 只用于统计和算法参考，不作为正确与否的唯一依据。

## 9. 学习统计

### 9.1 统计概览

`GET /statistics/overview?range=7D`

`range`：`7D | 30D`。

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "range": "7D",
    "totalLearned": 142,
    "totalMastered": 486,
    "reviewAccuracy": 86,
    "totalMinutes": 214,
    "daily": [
      { "date": "07-13", "label": "今天", "learned": 12, "reviewed": 28 }
    ],
    "masteryDistribution": [
      { "label": "已掌握", "value": 486, "color": "#2f7668" }
    ],
    "recentAchievements": [
      {
        "title": "连续学习 12 天",
        "description": "学习节奏已经开始稳定下来",
        "date": "今天"
      }
    ]
  }
}
```

正式接口可以不返回 `color`，由前端根据掌握程度映射颜色；当前字段仅为 Mock 展示便利而保留。

## 10. 建议业务错误码

| code | 含义 |
|---|---|
| `10001` | 参数校验失败 |
| `11001` | 用户名已存在 |
| `11002` | 邮箱已存在 |
| `11003` | 账号或密码错误 |
| `11004` | 登录会话失效 |
| `12001` | 单词不存在 |
| `13001` | 学习会话不存在或已结束 |
| `13002` | 回忆记录已提交 |
| `13003` | 今日没有待复习单词 |
| `90000` | 服务器内部错误 |

## 11. 前端实现位置

```text
frontend/src/api/client.ts              Axios、Token、Mock/真实请求切换
frontend/src/api/types.ts               全部共享 DTO 类型
frontend/src/api/modules/auth.ts        注册、登录、退出
frontend/src/api/modules/user.ts        当前用户和设置
frontend/src/api/modules/words.ts       词库和单词详情
frontend/src/api/modules/study.ts       首页、学习队列和回忆提交
frontend/src/api/modules/statistics.ts  学习统计
frontend/src/api/mockData.ts            开发期演示数据
```

后端接口实现后，将前端配置改为：

```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_USE_MOCK=false
```

然后重新启动 Vite 开发服务器。
