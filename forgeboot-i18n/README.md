# forgeboot-i18n-spring-boot-starter

国际化 (i18n) 支持模块

## 简介

`forgeboot-i18n-spring-boot-starter` 提供消息资源加载、动态刷新、注解支持等国际化能力。

## 核心功能

- **forgeboot-i18n-api**：定义消息服务接口。
- **forgeboot-i18n-impl**：基于 Spring `MessageSource` 的默认实现，支持多语言资源。
- **forgeboot-i18n-autoconfigure**：自动配置 `MessageSource`、配置属性读取。

## 引入依赖

Maven：
```xml
<dependency>
  <groupId>io.github.gewuyou</groupId>
  <artifactId>forgeboot-i18n-spring-boot-starter</artifactId>
  <version>${version}</version>
</dependency>
```
使用 Gradle：
```groovy
implementation "io.github.gewuyou:forgeboot-i18n-spring-boot-starter:${version}"
```

## 快速开始

在 `application.yml` 中添加：
```yaml
forgeboot:
  i18n:
    basename: messages
    default-locale: en_US
    cache-seconds: 3600
```
通过 `@Autowired Messages messages; messages.get("key");` 获取消息。

## 构建
```bash
./gradlew :forgeboot-i18n:build
```

## 许可

Apache-2.0
