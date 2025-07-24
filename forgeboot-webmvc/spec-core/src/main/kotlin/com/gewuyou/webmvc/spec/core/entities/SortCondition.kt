package com.gewuyou.webmvc.spec.core.entities

import com.gewuyou.webmvc.spec.core.enums.SortDirection

/**
 * 排序条件
 *
 * @author gewuyou
 * @since 2025-01-16 16:11:47
 */
class SortCondition {
    /**
     * 排序字段
     */
    var property: String = "createdAt"

    /**
     * 排序方向
     */
    var direction: SortDirection = SortDirection.DESC
}