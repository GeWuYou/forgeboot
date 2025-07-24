# forgeboot-banner

Spring Boot Banner 启动横幅模块

## 简介

`forgeboot-banner` 提供了一个可插拔的启动横幅（Banner）框架，与 Spring Boot 无缝集成，支持多种横幅渲染策略与配置项。你可以自定义文本、图片或多策略组合的 Banner，在应用启动时显示。

## 核心功能

- 可通过 `forgeboot.banner.strategy` 配置多种渲染策略（随机、按环境、按时间等）。
- 支持文本和图片两种 Banner 类型。
- 提供默认实现，也可自定义 `BannerStrategy` 和 `BannerProvider`。
- 自动集成 Spring Boot，启动时自动渲染。

## 引入依赖

使用 Maven：
```xml
<dependency>
  <groupId>io.github.gewuyou</groupId>
  <artifactId>forgeboot-banner</artifactId>
  <version>${version}</version>
</dependency>
```
使用 Gradle：
```groovy
implementation "io.github.gewuyou:forgeboot-banner:${version}"
```

## 子模块说明

- **forgeboot-banner-api**：核心接口与配置类（`BannerStrategy`, `BannerProvider`, `BannerProperties`）。
- **forgeboot-banner-impl**：默认实现，包括文件加载、日志输出等逻辑。
- **forgeboot-banner-launcher**：启动器模块，负责在 Spring Boot 启动阶段触发 Banner 渲染。

## 快速开始

1. 在 `application.yml` 中启用并配置：
   ```yaml
   forgeboot:
     banner:
       enabled: true
       strategy: RANDOM
       text-location: classpath:/banner.txt
       image-location: classpath:/banner.png
   ```
2. 启动应用，查看控制台或日志中的横幅效果。

## 构建
```bash
./gradlew :forgeboot-banner:build
```

## 许可

Apache-2.0
