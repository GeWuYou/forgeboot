package com.gewuyou.webmvc.spec.core.extension

import com.gewuyou.webmvc.spec.core.page.Sortable
import org.springframework.data.domain.Sort
import kotlin.collections.map

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
