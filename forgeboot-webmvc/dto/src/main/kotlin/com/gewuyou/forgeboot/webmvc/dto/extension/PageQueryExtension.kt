package com.gewuyou.forgeboot.webmvc.dto.extension


import com.gewuyou.forgeboot.webmvc.dto.enums.SortDirection
import com.gewuyou.forgeboot.webmvc.dto.request.PageQueryReq
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

/**
 * 标记属性用于模糊匹配查询条件
 * 
 * 该注解应用于实体类属性，表示在构建查询条件时使用LIKE操作符进行模糊匹配
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Like

/**
 * 标记属性用于精确匹配查询条件
 *
 * 该注解应用于实体类属性，表示在构建查询条件时使用EQUAL操作符进行精确匹配
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Equal

/**
 * 标记属性用于IN集合查询条件
 *
 * 该注解应用于实体类属性，表示在构建查询条件时使用IN操作符进行集合匹配
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class In

/**
 * 标记属性用于OR逻辑分组
 *
 * 该注解应用于实体类属性，表示该属性的查询条件需要与其他标记相同value值的属性
 * 进行OR逻辑组合，不同value值的组之间使用AND逻辑连接
 *
 * @property value 分组标识符，相同value的注解属性会被组合为一个OR条件组
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class OrGroup(val value: String)

/**
 * 分页查询扩展类
 *
 * 提供分页和查询条件构建的扩展方法，用于将 PageQueryReq 转换为 Spring Data 的分页对象，
 * 并生成通用的查询条件谓词列表。
 *
 * @since 2025-01-17
 * @author gewuyou
 */
fun <T> PageQueryReq<T>.toPageable(defaultSort: Sort.Order = Sort.Order.desc("createdAt")): Pageable {
    val orders = when {
        // 检查排序条件
        sortConditions.isNotEmpty() -> {
            sortConditions.map {
                if (it.direction == SortDirection.ASC) Sort.Order.asc(it.field) else Sort.Order.desc(it.field)
            }
        }
        // 检查是否有单字段排序
        sortBy.isNotBlank() -> {
            listOf(
                if (sortDirection == SortDirection.ASC) Sort.Order.asc(sortBy) else Sort.Order.desc(sortBy)
            )
        }
        // 如果都没有则默认按创建时间排序
        else -> listOf(defaultSort)
    }
    return PageRequest.of(currentPage - 1, pageSize, Sort.by(orders))
}

/**
 * 构建查询条件谓词列表
 *
 * 根据请求参数生成标准查询条件，包括时间范围、启用状态和删除状态等常用过滤条件。
 *
 * @param builder CriteriaBuilder 标准查询构造器
 * @param root Root<S> 查询的根对象
 * @param createAtName 创建时间字段名，默认 "createdAt"
 * @param enabledName 是否启用字段名，默认 "enabled"
 * @param deletedName 是否删除字段名，默认 "deleted"
 * @return MutableList<Predicate> 查询条件谓词列表
 */
fun <T, S> PageQueryReq<T>.getPredicates(
    builder: CriteriaBuilder,
    root: Root<S>,
    createAtName: String = "createdAt",
    enabledName: String = "enabled",
    deletedName: String = "deleted"
): MutableList<Predicate> {
    val predicates = mutableListOf<Predicate>()

    // 添加开始日期条件
    startDate?.let {
        predicates.add(builder.greaterThanOrEqualTo(root.get(createAtName), it))
    }

    // 添加结束日期条件
    endDate?.let {
        predicates.add(builder.lessThanOrEqualTo(root.get(createAtName), it))
    }

    // 添加是否启用条件
    enabled?.let {
        predicates.add(builder.equal(root.get<Boolean>(enabledName), it))
    }

    // 添加是否删除条件
    deleted.let {
        predicates.add(builder.equal(root.get<Boolean>(deletedName), it))
    }

    return predicates
}