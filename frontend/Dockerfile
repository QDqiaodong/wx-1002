FROM docker.io/node:20-alpine AS builder

RUN mkdir -p /app
WORKDIR /app

COPY package*.json ./

RUN npm config set registry https://registry.npmmirror.com/ && npm ci --no-audit

COPY . .

RUN npm run build

FROM docker.io/nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
