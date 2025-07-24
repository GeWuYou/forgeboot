# forgeboot-webmvc-dto

> 📦 Web 项目通用 DTO 模块，包含统一响应封装、分页/排序/过滤模型、JPA 查询构造扩展等内容。

---

## 🧩 简介

`forgeboot-webmvc-dto` 是 ForgeBoot 提供的 Web 项目 DTO 抽象模块，封装了常用的分页、排序、状态过滤等通用接口与请求模型，并提供统一的响应结构与查询构建工具，助力快速开发一致性良好的接口风格。

---

## ✨ 核心功能

- ✅ 标准化 API 响应结构：`BaseResult` / `PageResult` / `R`
- ✅ 通用分页/排序/过滤参数模型：`Pageable`, `Sortable`, `StatusFilterable`
- ✅ 查询构件组合接口：`QueryComponent` + 多种 Filterable 接口
- ✅ JPA 查询扩展支持：`JpaPredicateExtensions`, `PredicateExtensions`
- ✅ 响应信息国际化扩展：`I18nResult`
- ✅ 日期范围、关键字等实体查询条件模型

---

## 📦 引入依赖

使用 Maven：

```xml
<dependency>
    <groupId>io.github.gewuyou</groupId>
    <artifactId>forgeboot-webmvc-dto</artifactId>
    <version>${version}</version>
</dependency>
```

使用 Gradle：

```groovy
implementation "io.github.gewuyou:forgeboot-webmvc-dto:${version}"
```

---

## 🚀 快速开始

### ✅ 统一响应使用示例

```kotlin
@GetMapping("/hello")
fun hello(): R<String> {
    return R.ok("Hello, world!")
}
```

### ✅ 构建分页查询请求

```kotlin
data class UserPageRequest(
    override val currentPage: Int,
    override val pageSize: Int,
    override val keyword: String?
) : Pageable, KeywordSearchable
```

结合 JPA Predicate 扩展：

```kotlin
fun buildPredicate(request: UserPageRequest): Predicate {
    return where {
        orLike("username", request.keyword)
        andEqual("status", "ACTIVE")
    }
}
```

---

## 📘 高级用法

### 🔄 自动封装分页响应

```kotlin
fun <T> toPageResult(page: Page<T>): PageResult<T> {
    return PageResult.of(page)
}
```

控制器示例：

```kotlin
@GetMapping("/users")
fun listUsers(request: UserPageRequest): PageResult<UserDTO> {
    val page = userService.query(request)
    return PageResult.of(page)
}
```

### 🔍 自定义查询组件组合

你可以组合多种查询接口，让请求类直接实现 `QueryComponent`，构建强类型的 DSL 查询请求对象：

```kotlin
data class ProductQueryRequest(
    override val currentPage: Int,
    override val pageSize: Int,
    override val keyword: String?,
    override val status: Int?,
    override val dateRange: DateRangeCondition?
) : QueryComponent, Pageable, KeywordSearchable, StatusFilterable, DateRangeFilterable
```

一旦实现了这些接口，你就可以直接在 Controller 中使用 `.toJpaQuery()` 方法，无需再手动构建查询逻辑：

```kotlin
@GetMapping("/products")
fun query(request: ProductQueryRequest): PageResult<ProductDTO> {
    val (spec, pageRequest) = request.toJpaQuery<ProductEntity>()
    val page = productRepository.findAll(spec, pageRequest)
    return PageResult.of(page.map { it.toDTO() })
}
```

> ✅ 会自动根据你实现的接口生成分页、排序、关键字搜索、状态过滤、日期范围等查询条件，无需重复写繁琐的 JPA 条件拼接逻辑。


---

## 🔍 JPA 查询构造器扩展

你可以使用 `QueryComponent.toJpaQuery()` 一键将请求对象转换为 JPA 查询规范和分页条件。

### ✅ 示例请求类

```kotlin
data class ProductQueryRequest(
    override val currentPage: Int,
    override val pageSize: Int,
    override val keyword: String?,
    override val status: Int?,
    override val dateRange: DateRangeCondition?
) : Pageable, Sortable, KeywordSearchable, StatusFilterable, DateRangeFilterable
```

### ✅ 控制器代码（极简）

```kotlin
@GetMapping("/products")
fun query(request: ProductQueryRequest): PageResult<ProductDTO> {
    val (spec, pageRequest) = request.toJpaQuery<ProductEntity>()
    val page = productRepository.findAll(spec, pageRequest)
    return PageResult.of(page.map { it.toDTO() })
}
```

### ✅ 内部处理逻辑（你无需手写）

- 组合关键字模糊查询：`like lower(name)`、`like lower(description)`
- 处理状态过滤：`where status = ?`
- 构建日期范围：`createdAt >= ? and createdAt <= ?`
- 构建排序条件（多字段或默认排序）
- 构建分页信息（从 `currentPage` 开始，默认每页 10 条）

> 默认行为：如果请求未实现 `Pageable` 或 `Sortable`，会使用默认分页第 1 页、每页 10 条，无排序。

---

## 🧩 扩展组件介绍



| 模块                       | 说明                         |
|--------------------------|----------------------------|
| `BaseResult`             | 基础响应模型（带状态码与信息）            |
| `PageResult`             | 分页响应结构                     |
| `R`                      | 快捷响应构造器（等价于 Result.ok/err） |
| `Pageable`               | 分页能力接口                     |
| `Sortable`               | 排序参数接口                     |
| `Filterable`             | 通用过滤接口（可自定义字段过滤）           |
| `QueryComponent`         | 查询字段组合容器（支持 DSL 构造）        |
| `DateRangeCondition`     | 日期范围过滤器                    |
| `JpaPredicateExtensions` | JPA 查询构建扩展函数集              |



---

## 📄 许可协议

本项目采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源协议。

---

## 📬 联系作者

由 [@gewuyou](https://github.com/gewuyou) 维护。

欢迎提交 Issue 或 PR 改进本模块！
