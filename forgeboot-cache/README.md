# forgeboot-cache-spring-boot-starter

Spring Boot 缓存自动配置模块

## 简介

`forgeboot-cache-spring-boot-starter` 提供基于 Spring Cache 的一站式缓存解决方案，封装了常用的缓存接口、默认实现及自动配置。

## 核心功能

- **forgeboot-cache-api**：缓存接口定义，统一 `CacheManager`、`Cache` 操作。
- **forgeboot-cache-impl**：默认内存或 Redis 实现。
- **forgeboot-cache-autoconfigure**：Spring Boot 自动配置，读取 `forgeboot.cache.*` 配置属性，自动创建 `CacheManager`。

## 引入依赖

使用 Maven：
```xml
<dependency>
  <groupId>io.github.gewuyou</groupId>
  <artifactId>forgeboot-cache-spring-boot-starter</artifactId>
  <version>${version}</version>
</dependency>
```
使用 Gradle：
```groovy
implementation "io.github.gewuyou:forgeboot-cache-spring-boot-starter:${version}"
```

## 配置示例
```yaml
forgeboot:
  cache:
    type: redis
    redis:
      host: localhost
      port: 6379
    caffeine:
      spec: maximumSize=1000,expireAfterAccess=5m
```

## 快速开始

1. 添加依赖并配置缓存类型。
2. 在 Service 方法上使用 `@Cacheable("cacheName")`、`@CacheEvict` 等注解即可生效。

## 构建
```bash
./gradlew :forgeboot-cache:build
```

## 许可

Apache-2.0
