package com.gewuyou.webmvc.spec.jpa.extension


import com.gewuyou.webmvc.spec.core.enums.SortDirection
import com.gewuyou.webmvc.spec.core.page.DateRangeFilterable
import com.gewuyou.webmvc.spec.core.page.KeywordSearchable
import com.gewuyou.webmvc.spec.core.page.Pageable
import com.gewuyou.webmvc.spec.core.page.QueryComponent
import com.gewuyou.webmvc.spec.core.page.Sortable
import com.gewuyou.webmvc.spec.jpa.page.JpaFilterable
import com.gewuyou.webmvc.spec.jpa.page.JpaStatusFilterable
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import java.time.Instant


/**
 * 将自定义的排序方向枚举转换为 Spring Data JPA 的排序方向
 *
 * @return 对应的 Spring Data Sort.Direction 枚举值
 */
fun SortDirection.toSpringDirection(): Sort.Direction =
    if (this == SortDirection.DESC) Sort.Direction.DESC else Sort.Direction.ASC

/**
 * 将当前排序配置转换为可用于 Spring Data Pageable 的排序条件列表
 *
 * @param defaultSort 当没有其他排序条件时使用的默认排序，默认为按 createdAt 降序
 * @return 适用于 Pageable 的排序条件列表
 *
 * 处理逻辑优先级：
 * 1. 如果存在多个排序条件，使用 sortConditions 构建排序列表
 * 2. 如果指定了单一排序字段，使用 sortBy 和 sortDirection 构建排序条件
 * 3. 如果没有指定任何排序条件，使用默认排序
 */
fun Sortable.toSortOrders(defaultSort: Sort.Order = Sort.Order.desc("createdAt")): List<Sort.Order> {
    return when {
        sortConditions.isNotEmpty() -> {
            // 使用 map 将每个 SortCondition 转换为 Spring 的 Sort.Order 对象
            sortConditions.map {
                Sort.Order(
                    it.direction.toSpringDirection(),
                    it.property
                )
            }
        }

        sortBy.isNotBlank() -> {
            // 当存在单一排序字段时，创建对应的排序条件列表
            listOf(Sort.Order(sortDirection.toSpringDirection(), sortBy))
        }

        else -> listOf(defaultSort)
    }
}

/**
 * 构建关键字搜索对应的 JPA Predicate 条件列表
 *
 * 该扩展方法用于为实现了 KeywordSearchable 接口的对象构建 JPA 查询中的关键字搜索条件。
 * 遍历 keywordSearchFields() 提供的字段列表，对每个字段执行不区分大小写的模糊匹配查询。
 *
 * @param root 实体类的 JPA 查询根对象，用于获取实体字段
 * @param cb JPA 的 CriteriaBuilder 实例，用于构建查询条件
 * @return 返回构建的 [Predicate] 条件列表，若关键字为空则返回空列表
 *
 * 重要逻辑说明：
 * 1. 如果 keyword 为空或空白字符串，直接返回空列表，跳过所有构建逻辑
 * 2. 对每个 keywordSearchFields() 提供的字段尝试构建模糊匹配条件
 * 3. 使用 lower() 方法实现不区分大小写的比较
 * 4. 如果字段路径获取失败（如字段不存在），捕获异常并跳过该字段
 */
fun <T> KeywordSearchable.buildKeywordPredicates(
    root: Root<T>,
    cb: CriteriaBuilder,
): List<Predicate> {
    val kw = keyword?.trim()?.takeIf { it.isNotEmpty() } ?: return emptyList()
    return keywordSearchFields().mapNotNull { field ->
        try {
            val path = root.get<String>(field)
            cb.like(cb.lower(path), "%${kw.lowercase()}%")
        } catch (_: Exception) {
            null
        }
    }
}

/**
 * 根据 DateRangeFilterable 构建 JPA 日期范围谓词
 *
 * 该扩展方法用于为实现了 DateRangeFilterable 接口的对象构建 JPA 查询中的日期范围过滤条件。
 * 遍历 dataRangeFields 字段列表，为每个字段根据提供的 startDate 和 endDate 创建对应的谓词条件。
 *
 * @param root 实体类的 JPA 查询根对象，用于获取实体字段
 * @param cb JPA 的 CriteriaBuilder 实例，用于构建查询条件
 * @return 返回构建的 [Predicate] 条件列表，若未设置日期范围则返回空列表
 */
fun <T> DateRangeFilterable.buildDateRangePredicates(
    root: Root<T>,
    cb: CriteriaBuilder,
): List<Predicate> {
    val predicates = mutableListOf<Predicate>()
    this.dateRanges().forEach { condition ->
        /**
         * 获取数据库字段的 Instant 类型路径，用于构建时间范围查询条件
         */
        val dbField = root.get<Instant>(condition.fieldName)

        /**
         * 如果 startDate 不为 null，添加大于等于该日期时间的谓词条件
         */
        predicates.addIfNotNull(condition.startDate) {
            cb.greaterThanOrEqualTo(dbField, it)
        }

        /**
         * 如果 endDate 不为 null，添加小于等于该日期时间的谓词条件
         */
        predicates.addIfNotNull(condition.endDate) {
            cb.lessThanOrEqualTo(dbField, it)
        }
    }
    return predicates
}


/**
 * 将 QueryComponent 转换为 JPA 查询所需的 Specification（查询规范）
 *
 * 该扩展方法将实现了不同过滤接口的 QueryComponent 对象转换为 Spring Data JPA 兼容的 Specification。
 * 转换过程包含以下主要步骤：
 * 1. 检查对象是否实现了 DateRangeFilterable 接口，并构建日期范围查询条件
 * 2. 检查对象是否实现了 StatusFilterable 接口，并构建状态过滤查询条件
 * 3. 检查对象是否实现了 KeywordSearchable 接口，并构建关键字搜索查询条件
 * 4. 检查对象是否实现了 Filterable 接口，并使用其自定义的查询规范
 * 5. 将所有查询条件组合为一个逻辑 AND 条件返回
 *
 * @return 返回构建的 Specification<T> 查询规范
 */
fun <T> QueryComponent.toSpecification(): Specification<T> {
    return Specification { root, _, cb ->
        val predicates = mutableListOf<Predicate>()
        // 判断并组合 DateRange
        (this as? DateRangeFilterable)?.let {
            predicates += it.buildDateRangePredicates(root, cb)
        }
        // 判断并组合 JpaStatusFilterable
        (this as? JpaStatusFilterable)?.let {
            predicates += it.buildStatusPredicates(root, cb)
        }
        // 判断并组合 KeywordSearchable
        (this as? KeywordSearchable)?.let {
            predicates += it.buildKeywordPredicates(root, cb)
        }
        // 判断并组合 JpaFilterable
        @Suppress("UNCHECKED_CAST")
        (this as? JpaFilterable<*, T>)?.let {
            predicates += it.buildSpecification(root, cb)
        }
        // 组合所有查询条件，返回逻辑 AND 结果
        cb.and(*predicates.toTypedArray())
    }
}

/**
 * 将 QueryComponent 转换为 JPA 查询所需的 Specification 和分页请求对象
 *
 * 该扩展方法主要用于将自定义的查询组件对象转换为 Spring Data JPA 兼容的查询规范和分页参数。
 * 转换过程包含以下主要步骤：
 * 1. 通过 toSpecification 方法构建查询条件规范 (Specification)
 * 2. 从对象中提取分页参数 (Pageable)，并转换为 Spring Data 的 PageRequest 实例
 * 3. 从对象中提取排序信息 (Sortable)，并结合 toSortOrders 方法构建排序条件
 *
 * @return 返回一个 Pair，包含以下两个元素：
 *         - Specification<T>：JPA 查询条件规范
 *         - PageRequest：包含分页和排序信息的请求对象
 *
 * 重要逻辑说明：
 * 1. currentPage 从 1 开始计数，转换为 Spring Data 从 0 开始的页码
 * 2. pageSize 保持不变，直接用于分页请求
 * 3. 如果对象实现了 Sortable 接口，则使用其排序条件；否则使用未排序状态
 * 4. 如果对象未实现 Pageable 接口，则使用默认分页参数（第一页，每页10条）
 */
fun <T> QueryComponent.toJpaQuery(): Pair<Specification<T>, PageRequest> {
    val specification = this.toSpecification<T>()
    val pageable = this as? Pageable
    val sortable = this as? Sortable
    // 默认分页参数
    val page = pageable?.let { (it.currentPage - 1).coerceAtLeast(0) } ?: 0
    val size = pageable?.pageSize ?: 10
    // 排序规则
    val sort = sortable?.let { Sort.by(it.toSortOrders()) } ?: Sort.unsorted()
    return specification to PageRequest.of(page, size, sort)
}