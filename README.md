# Vocab App

一个基于 Vue 3、Spring Boot 4 和 MySQL 的前后端分离背单词网页应用。

项目计划从大学英语四级、六级词汇中筛选出不属于高考英语考纲的词汇，建立独立词库，并提供注册登录、单词学习、学习进度记录和间隔复习功能。

## 项目状态

当前已完成前端 MVP，后端业务接口待实现。

已完成：

- [x] GitHub 仓库初始化
- [x] Vue 3 前端项目初始化
- [x] Spring Boot 4 后端项目初始化
- [x] MySQL 数据库连接配置
- [x] Flyway 数据库迁移配置
- [x] 用户表初始迁移
- [x] 登录、注册与页面权限路由
- [x] 今日学习、新词、复习、词库、统计和设置页面
- [x] Mock API 与真实接口切换层
- [x] 前后端接口文档

计划实现：

- [ ] 后端用户注册
- [ ] 后端登录与 JWT 身份认证
- [ ] 后端单词库管理
- [ ] 后端学习与复习算法
- [ ] 后端用户学习进度
- [ ] 后端学习统计
- [ ] 四六级与高考词表差集处理

## 技术栈

### 前端

- Vue 3
- TypeScript
- Vite
- Axios
- Element Plus
- Vue Router
- Pinia

### 后端

- Java 26
- Spring Boot 4.1
- Spring Web MVC
- Spring Security
- Spring Data JPA
- Jakarta Validation
- Flyway
- Maven Wrapper

### 数据库

- MySQL 9.7
- 数据库名称：`vocab_app`
- 默认端口：`3306`
- 字符集：`utf8mb4`

## 项目结构

```text
.
├── frontend/                  Vue 前端
│   ├── public/
│   ├── src/
│   ├── .env.development
│   ├── package.json
│   └── vite.config.ts
│
├── backend/                   Spring Boot 后端
│   ├── .mvn/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   │       ├── db/migration/
│   │   │       ├── application.yml
│   │   │       └── application-local.yml
│   │   └── test/
│   ├── mvnw
│   ├── mvnw.cmd
│   └── pom.xml
├── docs/
│   └── API.md                 前后端接口约定
│
└── README.md
```

## 接口文档

前端预留接口、请求响应示例、错误码和联调约定见 [`docs/API.md`](docs/API.md)。

后端尚未实现时，前端通过 `VITE_USE_MOCK=true` 使用演示数据；接口完成后改为 `false` 即可联调。

## 本地地址

| 服务 | 地址 |
|---|---|
| Vue 前端 | `http://localhost:5173` |
| Spring Boot 后端 | `http://localhost:8080/api` |
| 健康检查 | `http://localhost:8080/api/actuator/health` |
| MySQL | `localhost:3306` |

由于 Spring Security 尚未完成业务配置，访问部分后端地址时可能返回 `401 Unauthorized`。这通常表示后端已经启动，但请求被默认安全规则拦截。

## 环境要求

开始运行项目前，请确认本机已经安装：

- Git
- Node.js 24.12 或更高版本
- npm
- Java 26
- MySQL 8.0 或更高版本

检查版本：

```cmd
git --version
node --version
npm.cmd --version
java -version
```

项目后端使用 Maven Wrapper，因此不需要额外安装全局 Maven。

## 数据库初始化

### 1. 登录 MySQL

Windows CMD：

```cmd
"D:\mysql\mysql-9.7.1-winx64\bin\mysql.exe" -h localhost -P 3306 -u root -p
```

输入 root 密码后创建数据库：

```sql
CREATE DATABASE vocab_app
CHARACTER SET utf8mb4
COLLATE utf8mb4_0900_ai_ci;
```

### 2. 创建项目账号

请将示例密码替换为自己的本地密码：

```sql
CREATE USER 'vocab_user'@'localhost'
IDENTIFIED BY 'CHANGE_ME';

GRANT ALL PRIVILEGES ON vocab_app.*
TO 'vocab_user'@'localhost';
```

检查权限：

```sql
SHOW GRANTS FOR 'vocab_user'@'localhost';
```

真实密码不得写入 Git 仓库。

## 启动后端

进入后端目录：

```cmd
cd backend
```

在 Windows CMD 中设置数据库环境变量：

```cmd
set "DB_USERNAME=vocab_user"
set "DB_PASSWORD=你的数据库密码"
```

运行后端：

```cmd
mvnw.cmd spring-boot:run
```

后端默认启动在：

```text
http://localhost:8080/api
```

停止服务：

```text
Ctrl + C
```

### PowerShell 环境变量写法

如果使用 PowerShell：

```powershell
$env:DB_USERNAME="vocab_user"
$env:DB_PASSWORD="你的数据库密码"
.\mvnw.cmd spring-boot:run
```

## 启动前端

打开另一个终端，进入前端目录：

```cmd
cd frontend
```

安装依赖：

```cmd
npm.cmd install
```

启动开发服务器：

```cmd
npm.cmd run dev
```

浏览器访问：

```text
http://localhost:5173
```

前端通过以下环境变量连接后端：

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

该变量保存在：

```text
frontend/.env.development
```

所有以 `VITE_` 开头的变量都会进入浏览器代码，因此不能在其中保存密码、Token 或其他敏感信息。

## 构建与测试

### 前端构建

```cmd
cd frontend
npm.cmd run build
```

### 前端类型检查

```cmd
cd frontend
npm.cmd run type-check
```

### 后端测试

运行测试前先设置数据库密码：

```cmd
cd backend
set "DB_PASSWORD=你的数据库密码"
mvnw.cmd test
```

### 后端打包

```cmd
cd backend
mvnw.cmd clean package
```

构建产物位于：

```text
backend/target/
```

## 数据库迁移

数据库结构通过 Flyway 管理，迁移文件位于：

```text
backend/src/main/resources/db/migration/
```

文件命名示例：

```text
V1__create_users.sql
V2__create_words.sql
V3__create_word_details.sql
V4__create_user_word_progress.sql
V5__create_review_logs.sql
```

已经在数据库中执行过的迁移文件不应直接修改。需要调整数据库结构时，应增加新的迁移文件。

## 核心数据设计

项目计划包含以下核心数据：

- 用户账号
- 用户学习设置
- 单词基础信息
- 单词词性与释义
- 单词用法与搭配
- 单词例句
- 词库来源与标签
- 用户单词学习进度
- 每次复习记录
- 每日学习统计

目标词库计算方式：

```text
目标词库 =（四级词汇 ∪ 六级词汇）－ 高考英语考纲词汇
```

词库处理还需要考虑：

- 大小写统一
- 单词词形还原
- 四六级重复词
- 英式和美式拼写
- 单词与短语的区别
- 词表版本差异
- 数据来源和许可证

项目不会直接复制商业词典的释义、例句或用法内容。词库数据应来自许可明确的公开数据、自行整理的内容或获得授权的数据源。

## Git 工作流

默认分支：

```text
main
```

当前开发分支示例：

```text
feature/project-init
feature/auth
feature/word-bank
feature/review
```

提交信息示例：

```text
feat: add user registration
feat: add word detail API
fix: prevent duplicate review records
test: add authentication tests
docs: update local setup guide
chore: update project configuration
```

建议每个功能通过独立分支和 Pull Request 合并到 `main`。

## 安全要求

请勿提交以下内容：

- MySQL 密码
- root 密码
- JWT 密钥
- GitHub Token
- 生产环境配置
- 包含敏感信息的 `.env` 文件
- `node_modules`
- `frontend/dist`
- `backend/target`

提交前建议运行：

```cmd
git status
git diff --cached
```

确认暂存内容中没有密码或其他敏感信息。

## 后续开发顺序

1. 配置 Spring Security。
2. 实现用户实体与 Repository。
3. 实现用户注册和密码加密。
4. 实现登录和 JWT。
5. 创建 Vue 登录、注册页面。
6. 建立单词库数据结构。
7. 实现单词搜索和详情。
8. 实现每日学习任务。
9. 实现复习计划与学习记录。
10. 实现统计页面。
11. 导入并校验正式词库。

## License

项目许可证尚未确定。

在仓库添加正式 `LICENSE` 文件之前，请勿假设项目代码或词库数据可以被自由复制、分发或商用。# -app
