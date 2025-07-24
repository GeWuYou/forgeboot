# forgeboot

多模块 Spring Boot 工具与 Starter 集合

## 项目概览

`forgeboot` 是一套面向 Spring Boot 的通用基础库，包含 Banner、缓存、上下文传播、核心工具、国际化、
调用链追踪、Web MVC 公共组件以及示例模块。旨在帮助团队快速搭建企业级微服务应用。

## 模块列表

| 模块名                                          | 功能简介                     |
|-------------------------------------------------|------------------------------|
| forgeboot-banner                                | 启动横幅 Banner 管理         |
| forgeboot-cache-spring-boot-starter             | 缓存自动配置                 |
| forgeboot-context-spring-boot-starter           | 上下文传播                   |
| forgeboot-core                                  | 核心扩展与序列化             |
| forgeboot-i18n-spring-boot-starter              | 国际化支持                   |
| forgeboot-trace-spring-boot-starter             | 分布式调用链追踪             |
| forgeboot-webmvc-spring-boot-starter            | Web MVC 公共组件集合         |
| forgeboot-demo                                  | 各模块功能示例               |

## 快速开始

1. 克隆仓库：
   ```bash
   git clone https://github.com/GeWuYou/forgeboot.git
   ```
2. 构建所有模块：
   ```bash
   ./gradlew build
   ```
## 构建与发布

- **构建**  
  ```bash
  ./gradlew clean build
  ```
- **发布到 Maven 仓库**  
  已配置 `maven-publish` 与 `axion-release`，只需执行：
  ```bash
  ./gradlew release
  ```

## 贡献指南

欢迎提交 Issue 和 PR，共同完善各模块功能与文档。

## 许可

Apache-2.0
