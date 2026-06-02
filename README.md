# ShortLink 短链接系统

ShortLink 是一个前后端分离的短链接系统，支持短链接创建、分组管理、回收站、访问跳转、访问统计、二维码预览、URL 标题获取、favicon 展示和 URL 安全校验。

后端采用 Spring Cloud 微服务架构，包含网关服务、后台管理服务和短链接核心服务；前端采用 Vue3 + Vite + Element Plus。

## 主要功能

- 用户注册、登录、退出登录
- 短链接创建、修改、删除、分页查询
- 分组管理和回收站
- 短链接跳转和访问统计
- 二维码生成和预览
- URL 标题、favicon 自动获取
- Redis 缓存、BloomFilter、分布式锁
- RabbitMQ 异步统计和安全检测
- ShardingSphere JDBC 分库分表

## 技术栈

- 后端：Java 17、Spring Boot 3、Spring Cloud、Gateway、OpenFeign、MyBatis-Plus、ShardingSphere、Redis、Redisson、RabbitMQ、Nacos、JWT、MySQL
- 前端：Vue3、Vite、Element Plus、Axios、Vue Router、Vuex、ECharts
- 其他：Hutool、Guava、Jsoup、ZXing、阿里云 OSS、Knife4j

## 项目结构

```text
.
├── shortlink
│   ├── admin                    # 后台管理服务，端口 8001
│   ├── common                   # 公共模块
│   ├── gateway                  # 网关服务，端口 8000
│   ├── project
│   │   ├── project-api          # Feign 接口
│   │   ├── project-dto          # DTO 模块
│   │   └── project-service      # 短链接核心服务，端口 8002
│   └── sql                      # 数据库脚本
└── short-link-console-vue       # 前端控制台
```

## 环境要求

- JDK 17
- Maven 3.8+
- Node.js 16+
- MySQL 8
- Redis
- RabbitMQ
- Nacos 2.2+

## 快速启动

### 1. 初始化数据库

创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS short_link_admin DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS short_link_server DEFAULT CHARACTER SET utf8mb4;
```

导入 SQL：

```text
shortlink/sql/init/short_link_admin.sql
shortlink/sql/init/short_link_server.sql
```

### 2. 修改本地配置

修改 MySQL 连接信息：

```text
shortlink/admin/src/main/resources/shardingsphere-local.yaml
shortlink/project/project-service/src/main/resources/shardingsphere-local.yaml
```

启动 MySQL、Redis、RabbitMQ、Nacos。local 环境默认使用 Nacos `dev` namespace，如需调整可修改：

```text
shortlink/admin/src/main/resources/bootstrap-local.yaml
shortlink/gateway/src/main/resources/bootstrap-local.yaml
shortlink/project/project-service/src/main/resources/bootstrap-local.yaml
```

Nacos 至少需要准备这些 YAML 配置：

```text
redis-shared.yaml
short-link-jwt.yaml
rabbitmq-conf.yaml
short-link-gateway-interceptor.yaml
```

最小模板如下，按本机环境替换即可：

```yaml
# redis-shared.yaml
spring:
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      password:
      database: 0
      timeout: 3000ms
```

```yaml
# short-link-jwt.yaml
short:
  link:
    jwt:
      secret-key: replace-with-a-random-secret
      ttl: 86400000
      token-name: Token
```

```yaml
# rabbitmq-conf.yaml
spring:
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest
    virtual-host: /
```

```yaml
# short-link-gateway-interceptor.yaml
short:
  link:
    gateway-access:
      enabled: true
      header-name: x-shortlink-gateway
      header-value: true
      exclude-path:
        - /page/**
        - /doc.html
        - /webjars/**
        - /swagger-ui/**
        - /v3/api-docs/**
        - /actuator/**
        - /api/short-link/admin/v1/user/login
        - /api/short-link/admin/v1/user/register
        - /api/short-link/admin/v1/user/has-username
        - /api/short-link/admin/v1/user/check-login
```

各模块依赖关系：

| 模块 | Nacos 配置 |
| --- | --- |
| gateway | `redis-shared.yaml`、`short-link-jwt.yaml`、`short-link-gateway-interceptor.yaml` |
| admin | `redis-shared.yaml`、`short-link-jwt.yaml`、`short-link-oss.yaml`、`rate.yaml` |
| project-service | `redis-shared.yaml`、`rabbitmq-conf.yaml`、`whitelist-domains.yaml`、`short-link-gaode.yaml` |

其中二维码、地理位置解析、URL 白名单等功能会用到：

```text
short-link-oss.yaml
whitelist-domains.yaml
short-link-gaode.yaml
rate.yaml
```

对应模板：

```yaml
# short-link-oss.yaml
short:
  link:
    oss:
      endpoint: your-oss-endpoint
      bucket: your-bucket
      region: your-region
      access-key-id: your-access-key-id
      access-key-secret: your-access-key-secret
```

```yaml
# whitelist-domains.yaml
short:
  link:
    url-check:
      enabled: true
      whitelist-enabled: false
      blacklist-domains: []
      whitelist-domains: []
      cache-seconds: 30
      tencent-cms:
        secret-id:
        secret-key:
        biz-type:
```

```yaml
# short-link-gaode.yaml
short:
  link:
    gaode:
      access-key:
      api-url: https://restapi.amap.com/v3/ip?ip=%s&output=json&key=%s
```

```yaml
# rate.yaml
short:
  link:
    rate-limit:
      enabled: true
      access-qps: 300
      create-qps: 30
      include-paths:
        - /api/short-link/**
      create-paths:
        - /api/short-link/admin/v1/create
        - /api/short-link/v1/create
```

### 3. 启动后端

```bash
cd shortlink
mvn clean install -DskipTests
```

分别启动：

```bash
mvn -pl project/project-service spring-boot:run
mvn -pl admin spring-boot:run
mvn -pl gateway spring-boot:run
```

服务端口：

```text
gateway: 8000
admin: 8001
project-service: 8002
```

### 4. 启动前端

```bash
cd short-link-console-vue
npm install
npm run dev
```

前端开发环境默认代理到：

```text
http://127.0.0.1:8000
```

如需修改，请调整：

```text
short-link-console-vue/.env.dev
```

## 访问说明

后台接口：

```text
http://localhost:8000/api/short-link/admin/v1/**
```

短链接核心接口：

```text
http://localhost:8000/api/short-link/v1/**
```

本地短链跳转：

```text
http://localhost:8002/{shortUri}
```

## 构建

后端：

```bash
cd shortlink
mvn clean package -DskipTests
```

前端：

```bash
cd short-link-console-vue
npm run build
```
