# forgeboot-trace-spring-boot-starter

> 🔍 分布式调用链追踪模块，基于上下文感知系统实现 requestId / traceId 的自动生成、注入与传播，适配协程、Reactor、线程池等多种执行模型。

---

## 🧩 简介

`forgeboot-trace-spring-boot-starter` 是 ForgeBoot 提供的轻量级调用链追踪模块，致力于在服务内部及分布式场景中提供统一的 traceId 管理机制。

该模块依赖 `forgeboot-context` 实现 traceId 的透明传递，支持与日志系统（MDC）、Web 框架（Servlet / WebFlux）无缝集成。

---

## ✨ 核心功能

- ✅ 支持全局唯一的 traceId/requestId 自动生成与注入
- ✅ 与日志系统集成（MDC 支持）
- ✅ 支持从 HTTP 请求头中自动提取 traceId
- ✅ 基于 `forgeboot-context` 实现跨线程 / 协程 / Reactor 任务 traceId 传递
- ✅ 可配置的 traceId 生成策略（内置 UUID，支持扩展如 OpenTelemetry）
- ✅ 适配 Spring Boot 自动装配，开箱即用

---

## 📦 引入依赖

使用 Maven：

```xml
<dependency>
  <groupId>io.github.gewuyou</groupId>
  <artifactId>forgeboot-trace-spring-boot-starter</artifactId>
  <version>${version}</version>
</dependency>
```

使用 Gradle：

```groovy
implementation "io.github.gewuyou:forgeboot-trace-spring-boot-starter:${version}"
```

---

## 🚀 快速开始

1. 引入依赖并启用自动配置（Spring Boot 自动生效）

2. 在日志配置中添加 traceId（以 logback 为例）：

    ```xml
    <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level traceId=%X{traceId} %logger{36} - %msg%n</pattern>
    ```

3. 可在任何业务代码中通过上下文访问：

```kotlin
val traceId = ContextHolder.get("traceId")
```

---

## ⚙️ 构建方式

使用 Gradle 命令进行构建发布：

```bash
./gradlew :forgeboot-trace:build
```

---

## 📄 许可

本项目采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源协议。

---

## 📬 联系作者

由 [@gewuyou](https://github.com/gewuyou) 维护。

欢迎提交 Issue 或 PR 改进本模块！
