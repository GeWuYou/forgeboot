package com.gewuyou.webmvc.spec.core.page

import com.gewuyou.webmvc.spec.core.entities.SortCondition
import com.gewuyou.webmvc.spec.core.enums.SortDirection

/**
 * 可排序接口，用于定义排序相关属性
 *
 * @since 2025-07-19 08:53:56
 * @author gewuyou
 */
interface Sortable : QueryComponent {
    /**
     * 获取排序字段名称
     */
    val sortBy: String

    /**
     * 获取排序方向（升序或降序）
     */
    val sortDirection: SortDirection

    /**
     * 获取排序条件列表，用于支持多条件排序
     */
    val sortConditions: List<SortCondition>
}