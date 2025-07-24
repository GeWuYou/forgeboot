# forgeboot-webmvc-spring-boot-starter

Web MVC 通用组件集合

## 简介

`forgeboot-webmvc-spring-boot-starter` 提供常用 Web 层组件，如版本管理、统一日志、全局异常处理、DTO 封装、参数校验、接口规范等。

## 核心模块

- **forgeboot-webmvc-version-spring-boot-starter**：API 版本控制  
- **forgeboot-webmvc-logger-spring-boot-starter**：请求/响应日志 AOP  
- **forgeboot-webmvc-exception-spring-boot-starter**：全局异常处理  
- **forgeboot-webmvc-exception-i18n-spring-boot-starter**：异常国际化  
- **forgeboot-webmvc-dto**：统一返回结构与分页工具  
- **forgeboot-webmvc-validation**：请求校验封装  
- **forgeboot-webmvc-spec**：自动生成接口规范文档

## 引入依赖

Maven：
```xml
<dependency>
  <groupId>io.github.gewuyou</groupId>
  <artifactId>forgeboot-webmvc-spring-boot-starter</artifactId>
  <version>${version}</version>
</dependency>
```
使用 Gradle：
```groovy
implementation "io.github.gewuyou:forgeboot-webmvc-spring-boot-starter:${version}"
```

## 快速开始

- 在 `application.yml` 中配置各模块开关和策略。  
- 使用 `BaseResult<T>` 或 `PageResult<T>` 封装返回值。  
- Controller 中使用 `@ApiVersion`, `@Validated` 等注解。

## 构建
```bash
./gradlew :forgeboot-webmvc:build
```

## 许可

Apache-2.0
