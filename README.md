# ForgeBoot

> 🚀 企业级 Spring Boot 基础设施框架，提供开箱即用的通用组件和最佳实践。

[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple.svg)](https://kotlinlang.org/)

---

## 📖 项目简介

`ForgeBoot` 是一套面向 Spring Boot 的企业级通用基础库，涵盖 **Banner 管理**、**多层缓存**、**上下文传播**、**分布式追踪**、*
*服务防护**、**文件存储**、**国际化**、**Web MVC 增强** 等多个领域。旨在帮助团队快速搭建生产级微服务应用，提升开发效率，统一技术栈。

---

## ✨ 核心特性

- 🎨 **开箱即用**：所有模块均提供 Spring Boot Starter，引入依赖即可使用
- 🔧 **灵活配置**：通过 `application.yml` 即可完成大部分配置
- 🌍 **国际化支持**：内置 i18n 模块，轻松实现多语言
- 🛡️ **服务防护**：集成限流、幂等、冷却、尝试限制等防护能力
- 📦 **统一响应**：标准化 API 响应结构，提升前后端协作效率
- 🔍 **全链路追踪**：自动生成并传播 traceId，支持多线程/协程/Reactor
- 💾 **多层缓存**：支持 Caffeine + Redis 组合缓存，提供缓存预热、过期策略等
- 📁 **文件存储**：抽象存储接口，支持 MinIO、OSS、S3 等多种后端
- 🚨 **全局异常处理**：统一异常拦截和响应格式
- 📝 **请求日志**：自动记录接口调用日志，支持敏感信息脱敏

---

## 📦 模块列表

### 🎯 核心模块

| 模块                                                   | 描述                    | 文档                                      |
|------------------------------------------------------|-----------------------|-----------------------------------------|
| [`forgeboot-banner`](forgeboot-banner/README.md:1)   | 启动横幅管理                | [README](forgeboot-banner/README.md:1)  |
| [`forgeboot-core`](forgeboot-core/README.md:1)       | 核心扩展与序列化              | [README](forgeboot-core/README.md:1)    |
| [`forgeboot-context`](forgeboot-context/README.md:1) | 上下文传播（多线程/协程/Reactor） | [README](forgeboot-context/README.md:1) |
| [`forgeboot-trace`](forgeboot-trace/README.md:1)     | 分布式调用链追踪              | [README](forgeboot-trace/README.md:1)   |

### 💾 数据与缓存

| 模块                                                   | 描述                     | 文档                                      |
|------------------------------------------------------|------------------------|-----------------------------------------|
| [`forgeboot-cache`](forgeboot-cache/README.md:1)     | 多层缓存（Caffeine + Redis） | [README](forgeboot-cache/README.md:1)   |
| [`forgeboot-storage`](forgeboot-storage/README.md:1) | 文件存储抽象（MinIO/OSS/S3）   | [README](forgeboot-storage/README.md:1) |

### 🛡️ 服务防护

| 模块                                                       | 描述                  | 文档                                        |
|----------------------------------------------------------|---------------------|-------------------------------------------|
| [`forgeboot-safeguard`](forgeboot-safeguard/README.md:1) | 服务防护（限流/幂等/冷却/尝试限制） | [README](forgeboot-safeguard/README.md:1) |

### 🌐 Web 增强

| 模块                                                                                      | 描述             | 文档                                                                |
|-----------------------------------------------------------------------------------------|----------------|-------------------------------------------------------------------|
| [`forgeboot-webmvc`](forgeboot-webmvc/README.md:1)                                      | Web MVC 通用组件集合 | [README](forgeboot-webmvc/README.md:1)                            |
| [`forgeboot-webmvc-dto`](forgeboot-webmvc/forgeboot-webmvc-dto/README.md:1)             | 统一响应封装与分页      | [README](forgeboot-webmvc/forgeboot-webmvc-dto/README.md:1)       |
| [`forgeboot-webmvc-exception`](forgeboot-webmvc/forgeboot-webmvc-exception/README.md:1) | 全局异常处理         | [README](forgeboot-webmvc/forgeboot-webmvc-exception/README.md:1) |
| [`forgeboot-webmvc-logger`](forgeboot-webmvc/forgeboot-webmvc-logger/README.md:1)       | 请求/响应日志 AOP    | [README](forgeboot-webmvc/forgeboot-webmvc-logger/README.md:1)    |
| `forgeboot-webmvc-validation`                                                           | 参数校验增强         | -                                                                 |

### 🌍 国际化

| 模块                                             | 描述    | 文档                                   |
|------------------------------------------------|-------|--------------------------------------|
| [`forgeboot-i18n`](forgeboot-i18n/README.md:1) | 国际化支持 | [README](forgeboot-i18n/README.md:1) |

### 📚 示例与演示

| 模块                                             | 描述      | 文档                                   |
|------------------------------------------------|---------|--------------------------------------|
| [`forgeboot-demo`](forgeboot-demo/README.md:1) | 各模块功能示例 | [README](forgeboot-demo/README.md:1) |

---

## 🚀 快速开始

### 前置要求

- JDK 17+
- Gradle 8.x 或 Maven 3.8+
- Redis 6.x+（缓存/防护模块需要）
- MinIO / OSS（存储模块可选）

### 1. 克隆仓库

```bash
git clone https://github.com/GeWuYou/forgeboot.git
cd forgeboot
```

### 2. 构建项目

```bash
# 使用 Gradle
./gradlew clean build

# 跳过测试
./gradlew clean build -x test
```

### 3. 运行示例

```bash
cd forgeboot-demo/forgeboot-trace-demo
./gradlew bootRun
```

访问 `http://localhost:8080` 查看效果。

---

## 📚 使用指南

### 引入依赖

以 `forgeboot-cache` 为例，在项目中引入：

**Gradle**：

```kotlin
dependencies {
    implementation("io.github.gewuyou:forgeboot-cache-spring-boot-starter:${version}")
}
```

**Maven**：

```xml

<dependency>
    <groupId>io.github.gewuyou</groupId>
    <artifactId>forgeboot-cache-spring-boot-starter</artifactId>
    <version>${version}</version>
</dependency>
```

### 配置示例

在 [`application.yml`](application.yml:1) 中配置：

```yaml
forgeboot:
  # 缓存配置
  cache:
    type: redis
    redis:
      host: localhost
      port: 6379
    caffeine:
      spec: maximumSize=1000,expireAfterAccess=5m

  # 调用链追踪
  trace:
    enabled: true

  # 服务防护
  safeguard:
    enabled: true

  # 文件存储
  storage:
    mode: minio
    endpoint: http://localhost:9000
    access-key: minioadmin
    secret-key: minioadmin
    bucket: uploads

  # Web MVC 增强
  webmvc:
    response:
      include-request-id: true
    exception:
      include-stack-trace: false
    logger:
      enabled: true
      level: INFO
```

### 代码示例

**使用缓存**：

```kotlin
@Service
class UserService {

    @Cacheable("users")
    fun findById(id: Long): User {
        return userRepository.findById(id)
    }

    @CacheEvict("users", key = "#id")
    fun deleteById(id: Long) {
        userRepository.deleteById(id)
    }
}
```

**使用限流**：

```kotlin
@RestController
class OrderController {

    @RateLimit(
        key = "#userId",
        capacity = "100",
        refillTokens = "10",
        refillPeriodMs = "1000"
    )
    @PostMapping("/order")
    fun createOrder(@RequestParam userId: String): ApiResponse {
        return orderService.create(userId)
    }
}
```

**使用文件存储**：

```kotlin
@Service
class FileService(
    private val storageComponent: FullFileStorageComponent
) {
    fun uploadFile(file: MultipartFile): String {
        val meta = storageComponent.uploadFile(file)
        return meta.storageKey
    }
}
```

---

## 🏗️ 架构设计

### 模块分层

```
forgeboot/
├── forgeboot-core/              # 核心层：通用工具、扩展、序列化
├── forgeboot-context/           # 上下文层：跨线程/协程上下文传播
├── forgeboot-trace/             # 追踪层：分布式调用链追踪
├── forgeboot-cache/             # 缓存层：多级缓存、缓存策略
├── forgeboot-storage/           # 存储层：文件存储抽象
├── forgeboot-safeguard/         # 防护层：限流、幂等、冷却
├── forgeboot-i18n/              # 国际化层：多语言支持
├── forgeboot-webmvc/            # Web 层：MVC 增强组件
└── forgeboot-demo/              # 示例层：各模块使用示例
```

### 设计原则

- **模块化**：每个模块职责单一，可独立使用
- **可扩展**：提供 SPI 机制，支持自定义实现
- **零侵入**：通过注解和配置使用，无需修改业务代码
- **高性能**：基于 Redis + Lua 脚本、Caffeine 本地缓存等高性能方案
- **生产就绪**：经过充分测试，适用于生产环境

---

## 🛠️ 构建与发布

### 本地构建

```bash
# 构建所有模块
./gradlew clean build

# 构建指定模块
./gradlew :forgeboot-cache:build

# 发布到本地 Maven 仓库
./gradlew publishToMavenLocal
```

### 发布到 Maven 中央仓库

```bash
# 使用 axion-release 插件管理版本
./gradlew release

# 发布到 Maven Central
./gradlew publish
```

---

## 🤝 贡献指南

欢迎贡献代码、提交 Issue 或改进文档！

### 贡献流程

1. Fork 本仓库
2. 创建特性分支：`git checkout -b feature/your-feature`
3. 提交更改：`git commit -am 'Add some feature'`
4. 推送到分支：`git push origin feature/your-feature`
5. 提交 Pull Request

### 开发规范

- 遵循 Kotlin 编码规范
- 编写单元测试，保证代码覆盖率
- 更新相关文档
- 提交信息清晰明确

---

## 📝 更新日志

查看 [CHANGELOG.md](CHANGELOG.md) 了解版本更新历史。

---

## 📄 许可证

本项目采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源协议。

```
Copyright 2024 GeWuYou

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

## 📬 联系方式

- **作者**：[@gewuyou](https://github.com/gewuyou)
- **项目地址**：[https://github.com/GeWuYou/forgeboot](https://github.com/GeWuYou/forgeboot)
- **问题反馈**：[GitHub Issues](https://github.com/GeWuYou/forgeboot/issues)

---


<div align="center">

**如果这个项目对你有帮助，请给一个 ⭐️ Star 支持一下！**

Made with ❤️ by [GeWuYou](https://github.com/gewuyou)

</div>
