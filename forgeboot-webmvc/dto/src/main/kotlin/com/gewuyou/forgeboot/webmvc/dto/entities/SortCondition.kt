package com.gewuyou.forgeboot.webmvc.dto.entities

import org.hibernate.query.SortDirection

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
    var direction: SortDirection = SortDirection.DESCENDING
}