# 前端部署说明

本项目生产环境域名规划如下：

- `https://admin.lnko.cn`：后台前端页面
- `https://api.lnko.cn/api/short-link/**`：后台接口，经 `gateway:8000`
- `https://lnko.cn/{shortUri}`：短链接访问，经 `project-service:8002`

## 1. 前端打包

安装依赖：

```bash
npm install
```

执行生产打包：

```bash
npm run build
```

打包产物输出到：

```bash
dist/
```

生产环境接口地址来自 [`.env.product`](./.env.product)：

```env
VITE_BASE_API='https://api.lnko.cn/api/short-link/admin'
```

所以后台页面在生产环境会直接请求：

```text
https://api.lnko.cn/api/short-link/admin/v1/**
```

## 2. 前端文件放置位置

将 `dist/` 下所有文件上传到 Nginx 容器或服务器中的前端静态目录，例如：

```bash
/usr/share/nginx/html
```

## 3. Nginx 配置文件放哪里

将下面完整配置放到：

```bash
/etc/nginx/conf.d/lnko.cn.conf
```

如果你习惯按站点拆文件，也可以拆成多个 `.conf`，但按这份文档，推荐直接使用一个完整文件：

```bash
/etc/nginx/conf.d/lnko.cn.conf
```

## 4. 完整 Nginx 配置

以下配置可直接联通前后端，证书路径使用你提供的实际路径：

```nginx
server {
    listen 80;
    server_name lnko.cn;
    return 301 https://$host$request_uri;
}

server {
    listen 80;
    server_name api.lnko.cn;
    return 301 https://$host$request_uri;
}

server {
    listen 80;
    server_name admin.lnko.cn;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    server_name lnko.cn;

    ssl_certificate     /etc/nginx/ssl/lnko.cn/lnko.cn_bundle.pem;
    ssl_certificate_key /etc/nginx/ssl/lnko.cn/lnko.cn.key;

    location / {
        proxy_pass http://shortlink-project:8002;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto https;
    }
}

server {
    listen 443 ssl http2;
    server_name api.lnko.cn;

    ssl_certificate     /etc/nginx/ssl/api.lnko.cn/api.lnko.cn_bundle.pem;
    ssl_certificate_key /etc/nginx/ssl/api.lnko.cn/api.lnko.cn.key;

    location /api/short-link/ {
        proxy_pass http://shortlink-gateway:8000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto https;
    }

    location / {
        return 404;
    }
}

server {
    listen 443 ssl http2;
    server_name admin.lnko.cn;

    ssl_certificate     /etc/nginx/ssl/admin.lnko.cn/admin.lnko.cn_bundle.pem;
    ssl_certificate_key /etc/nginx/ssl/admin.lnko.cn/admin.lnko.cn.key;

    root /usr/share/nginx/html;
    index index.html;

    location /assets/ {
        try_files $uri =404;
        access_log off;
        expires 7d;
        add_header Cache-Control "public, immutable";
    }

    location = /favicon.ico {
        try_files $uri =404;
        access_log off;
        expires 7d;
    }

    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

## 5. 使配置生效

检查 Nginx 配置：

```bash
nginx -t
```

重新加载配置：

```bash
nginx -s reload
```

## 6. 重要说明

如果后端相关容器发生过重启，例如：

- `shortlink-gateway`
- `shortlink-project`

建议同步重新加载一次 Nginx：

```bash
docker exec shortlink-nginx nginx -s reload
```

原因是当前配置中的反向代理使用了容器服务名，例如：

```nginx
proxy_pass http://shortlink-gateway:8000;
proxy_pass http://shortlink-project:8002;
```

当后端容器重启后，容器 IP 可能会变化。Nginx 如果仍然持有旧的解析结果，就可能出现：

- `502 Bad Gateway`
- `connect() failed (111: Connection refused) while connecting to upstream`

出现这类情况时，优先执行一次：

```bash
docker exec shortlink-nginx nginx -s reload
```
