# forgeboot-webmvc-spec

> 🧩 Web 项目通用 CRUD 接口规范模块，封装 Repository 与 Service 层的标准增删改查接口与默认实现，简化重复代码。

---

## 🧩 简介

`forgeboot-webmvc-spec` 是 ForgeBoot 提供的统一接口规范模块，旨在为常见的 Web 开发提供可复用的 Repository 和 Service 层增删改查基础接口及实现模板。

通过继承和组合该模块中的接口和基类，可以快速构建具有一致风格的业务组件，提升开发效率、降低重复代码、统一项目结构。

---

## ✨ 核心功能

- ✅ `CrudRepositorySpec<T, ID>`：通用 Repository 接口规范（继承自 `JpaRepository`）
- ✅ `CrudServiceSpec<T, ID>`：业务层接口标准化定义
- ✅ `CrudServiceImplSpec<T, ID>`：默认 Service 实现（依赖 Repository）
- ✅ 泛型支持 Entity 类型与主键类型的自动推导
- ✅ 与 DTO 模块 (`forgeboot-webmvc-dto`) 无缝协作

---

## 📦 引入依赖

使用 Maven：

```xml
<dependency>
  <groupId>io.github.gewuyou</groupId>
  <artifactId>forgeboot-webmvc-spec</artifactId>
  <version>${version}</version>
</dependency>
```

使用 Gradle：

```groovy
implementation "io.github.gewuyou:forgeboot-webmvc-spec:${version}"
```

---

## 🚀 快速开始

### ✅ 定义实体类

```kotlin
@Entity
data class Product(
    @Id val id: Long,
    val name: String,
    val status: Int
)
```

### ✅ 创建 Repository 接口

```kotlin
interface ProductRepository : CrudRepositorySpec<Product, Long>
```

### ✅ 创建 Service 接口

```kotlin
interface ProductService : CrudServiceSpec<Product, Long>
```

### ✅ 创建默认实现类

```kotlin
@Service
class ProductServiceImpl(
    override val repository: ProductRepository
) : CrudServiceImplSpec<Product, Long>(), ProductService
```

现在你就拥有了以下功能：
- 新增：`save(entity)`
- 修改：`updateById(id, modifier)`
- 删除：`deleteById(id)`
- 查询单个：`findById(id)`
- 查询全部：`findAll()`

---

## 🧩 扩展方法（默认实现中提供）

```kotlin
fun updateById(id: ID, modifier: (T) -> Unit): Boolean
fun existsById(id: ID): Boolean
fun deleteById(id: ID): Boolean
```

可自由覆写或组合使用。

---

## 🧠 与其他模块集成建议

| 模块                     | 集成方式说明                                    |
|------------------------|-------------------------------------------|
| `forgeboot-webmvc-dto` | 可配合 `PageResult`, `QueryComponent` 返回分页数据 |
---

## 📄 许可协议

本项目采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源协议。

---

## 📬 联系作者

由 [@gewuyou](https://github.com/gewuyou) 维护。

欢迎提交 Issue 或 PR 改进本模块！
