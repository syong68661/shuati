# Shuati

刷题系统项目仓库，包含前端 `shuati-frontend` 与后端 `shuati-backend` 两部分。

## 项目结构

```text
shuati/
|- shuati-backend/   Spring Boot 后端
|- shuati-frontend/  Next.js 前端
|- tools/            本地组件安装包（已忽略，不提交到 Git）
```

## 技术栈

### 后端

- Java 8
- Spring Boot 2.7.x
- MyBatis / MyBatis-Plus
- MySQL
- Redis
- Elasticsearch
- Sa-Token
- Nacos
- Sentinel

### 前端

- Next.js 14
- React 18
- TypeScript
- Ant Design 5
- Redux Toolkit

## 运行前准备

建议本地先准备以下依赖：

- JDK 8
- Maven 3.8+
- Node.js 18+
- MySQL
- Redis
- Elasticsearch

部分后端功能还依赖：

- Nacos
- Sentinel Dashboard
- 微信相关配置
- 腾讯云 COS 配置
- 火山引擎 AI 相关配置

## 快速启动

### 1. 启动后端

进入 `shuati-backend`，按实际环境修改 `src/main/resources/application.yml` 及相关多环境配置文件。

重点检查：

- MySQL 连接信息
- Redis 连接信息
- Elasticsearch 连接信息
- Nacos / Sentinel 配置
- COS、微信、AI 等第三方密钥

初始化数据库可参考：

- `shuati-backend/sql/create_table.sql`
- `shuati-backend/sql/create_table2.sql`
- `shuati-backend/sql/insert_data.sql`

启动命令：

```bash
cd shuati-backend
mvn spring-boot:run
```

默认接口文档地址：

```text
http://localhost:8101/api/doc.html
```

### 2. 启动前端

进入 `shuati-frontend`，安装依赖并启动：

```bash
cd shuati-frontend
npm install
npm run dev
```

默认访问地址：

```text
http://localhost:3000
```

如果前端请求后端接口失败，请检查前端请求地址配置以及后端服务是否已启动。

## 说明

- 根目录 `tools/` 下是本地开发时使用的组件安装包，不纳入 Git 仓库。
- 根目录演示视频已排除提交，避免仓库体积过大。
- 后端 `lib/hotkey-client-0.0.4-SNAPSHOT.jar` 当前被 `pom.xml` 以本地 `systemPath` 方式引用，构建时请保留该文件。

## 参考文档

- 后端说明：`shuati-backend/README.md`
- 前端说明：`shuati-frontend/README.md`
- 启动说明：`启动相关组件.docx`
