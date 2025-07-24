# forgeboot-context-spring-boot-starter

> 🧠 请求上下文感知模块，支持在多线程、Reactor、协程等异步场景中透明传递上下文字段，如 traceId、userId、token 等。

---

## 🧩 简介

`forgeboot-context-spring-boot-starter` 是 ForgeBoot 提供的基础上下文传播模块，旨在解决异步环境下上下文（如用户信息、请求信息）无法传递的问题。

支持多执行模型（ThreadLocal、Reactor Context、Kotlin Coroutine Context）无缝上下文共享，并通过统一接口抽象、自动装配、可扩展的字段注入机制，提升服务内的数据一致性与可维护性。

---

## ✨ 核心功能

- ✅ 支持上下文字段自动注入与解析（Header、MDC、自定义）
- ✅ 支持多执行模型（线程 / 协程 / Reactor）上下文透明传递
- ✅ 提供统一 `ContextHolder` 访问入口
- ✅ 提供字段注册器与注入策略 SPI 扩展
- ✅ 支持 Spring Web（Servlet）、WebFlux、Feign、RestTemplate 等组件
- ✅ 与 trace 模块无缝集成，自动传播 traceId/requestId

---

## 📦 引入依赖

使用 Maven：

```xml
<dependency>
  <groupId>io.github.gewuyou</groupId>
  <artifactId>forgeboot-context-spring-boot-starter</artifactId>
  <version>${version}</version>
</dependency>
```

使用 Gradle：

```groovy
implementation "io.github.gewuyou:forgeboot-context-spring-boot-starter:${version}"
```

---

## 🚀 快速开始

### ✅ 基础使用

```kotlin
@RestController
class DemoController(private val contextHolder: ContextHolder) {
    @GetMapping("/demo")
    fun demo(): String {
        val traceId = contextHolder.get("traceId")
        return "traceId: $traceId"
    }
}
```

---

## 🔌 可扩展点

你可以通过实现以下接口来自定义字段注入行为：

- `ContextProcessor`：用于从请求中提取字段
- `ContextFieldContributor`：用于注册要注入的上下文字段

示例：

```kotlin
@Component
class CustomContextFieldContributor : ContextFieldContributor {
    override fun contribute(registry: FieldRegistry) {
        registry.register("tenantId", Scope.THREAD_LOCAL)
    }
}
```

---

## ⚙️ 构建方式

使用 Gradle 命令进行构建发布：

```bash
./gradlew :forgeboot-context:build
```

---

## 📄 许可

本项目采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源协议。

---

## 📬 联系作者

由 [@gewuyou](https://github.com/gewuyou) 维护。

欢迎提交 Issue 或 PR 改进本模块！
