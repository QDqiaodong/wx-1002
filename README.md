# wx-1002 园区公共遮阳伞片区分组批量分配系统

## 项目简介

园区公共遮阳伞片区分组批量分配系统，用于管理遮阳伞档案、户外片区、单台或批量片区重分配和片区调整流水。

## 技术栈

- 前端：Vue 3 + Vite + Element Plus
- 后端：Spring Boot 3.3 + JDK 17 + Spring Data JPA
- 数据库：MySQL 8
- 缓存：Redis
- 部署：Docker + Docker Compose + Nginx

## 端口说明

| 服务 | 地址 |
| --- | --- |
| 前端 | http://localhost:3102 |
| 后端 API | http://localhost:8102/api |
| MySQL | 127.0.0.1:3312 |
| Redis | 127.0.0.1:6412 |

## 启动方式

```bash
docker compose up -d --build
```

## 单独编译

```bash
cd backend && mvn compile -q
cd ../frontend && npm ci && npm run build
```

## Docker 构建说明

前后端 Dockerfile 已按依赖层和源码层分离。首次构建会下载 Maven/npm 依赖，后续只改业务源码时会复用 Docker 原生分层缓存。

## 常见排错

- 端口占用：检查 `.env` 中的 `FRONTEND_PORT`、`BACKEND_PORT`、`MYSQL_PORT`、`REDIS_PORT`。
- 后端编译失败：先执行 `mvn -version`，再检查 JDK、Lombok 和 `maven-compiler-plugin`。
- 前端构建失败：先执行 `npm ci`，再按 Vite 输出检查 import、组件导出名和环境变量。
