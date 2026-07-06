#!/bin/bash

echo "======================================"
echo " 启动园区公共遮阳伞管理系统"
echo "======================================"

if [ ! -f ".env" ]; then
    echo "错误: .env 文件不存在"
    exit 1
fi

source .env

echo ""
echo "检查端口占用..."

if lsof -nP -iTCP:${FRONTEND_PORT} -sTCP:LISTEN >/dev/null 2>&1; then
    echo "错误: 前端端口 ${FRONTEND_PORT} 已被占用"
    lsof -nP -iTCP:${FRONTEND_PORT} -sTCP:LISTEN
    exit 1
fi

if lsof -nP -iTCP:${BACKEND_PORT} -sTCP:LISTEN >/dev/null 2>&1; then
    echo "错误: 后端端口 ${BACKEND_PORT} 已被占用"
    lsof -nP -iTCP:${BACKEND_PORT} -sTCP:LISTEN
    exit 1
fi

if lsof -nP -iTCP:${MYSQL_PORT} -sTCP:LISTEN >/dev/null 2>&1; then
    echo "错误: MySQL端口 ${MYSQL_PORT} 已被占用"
    lsof -nP -iTCP:${MYSQL_PORT} -sTCP:LISTEN
    exit 1
fi

if lsof -nP -iTCP:${REDIS_PORT} -sTCP:LISTEN >/dev/null 2>&1; then
    echo "错误: Redis端口 ${REDIS_PORT} 已被占用"
    lsof -nP -iTCP:${REDIS_PORT} -sTCP:LISTEN
    exit 1
fi

echo "端口检查通过"
echo ""

echo "开始构建并启动容器..."
docker-compose up --build -d

echo ""
echo "等待服务启动..."
sleep 15

echo ""
echo "======================================"
echo " 服务启动完成"
echo "======================================"
echo ""
echo "前端访问地址: http://localhost:${FRONTEND_PORT}"
echo "后端API地址: http://localhost:${BACKEND_PORT}/api"
echo "MySQL端口: ${MYSQL_PORT}"
echo "Redis端口: ${REDIS_PORT}"
echo ""
echo "验证访问:"

echo "- http://127.0.0.1:${FRONTEND_PORT}"
curl -sS http://127.0.0.1:${FRONTEND_PORT} | head -5

echo ""
echo "- http://localhost:${FRONTEND_PORT}"
curl -sS http://localhost:${FRONTEND_PORT} | head -5

echo ""
echo "======================================"