# forgeboot-core

通用核心库

## 简介

`forgeboot-core` 包含项目中通用的扩展、工具与序列化支持，例如通用枚举处理、序列化接口、基础配置等。

## 核心功能

- **forgeboot-core-extension**：公共扩展函数、集合工具类等。
- **forgeboot-core-serialization**：Jackson/Kotlin 序列化自定义注册与模块。

## 引入依赖

Maven：
```xml
<dependency>
  <groupId>io.github.gewuyou</groupId>
  <artifactId>forgeboot-core</artifactId>
  <version>${version}</version>
</dependency>
```
使用 Gradle：
```groovy
implementation "io.github.gewuyou:forgeboot-core:${version}"
```

## 快速开始

在 Spring Boot 中引入后，自动注册序列化模块，可直接使用扩展函数。

## 构建
```bash
./gradlew :forgeboot-core:build
```

## 许可

Apache-2.0
