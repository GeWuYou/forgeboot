package com.gewuyou.webmvc.spec.core.request

import com.gewuyou.webmvc.spec.core.page.DateRangeFilterable
import com.gewuyou.webmvc.spec.core.page.Pageable
import com.gewuyou.webmvc.spec.core.page.Sortable


/**
 * 抽象排序日期分页请求
 * 用于封装包含排序、分页和日期范围过滤条件的请求参数
 *
 * @property currentPage 当前页码，默认值为1
 * @property pageSize 每页记录数，默认值为10
 * @property sortBy 排序字段名称，默认值为"createdAt"
 * @property sortDirection 排序方向，默认值为降序(SortDirection.DESCENDING)
 * @property sortConditions 排序条件集合，默认值为空列表
 *
 * @since 2025-07-19 10:23:05
 * @author gewuyou
 */
abstract class AbstractSortedDatePageRequest(
    override val currentPage: Int = 1,
    override val pageSize: Int = 10,
) : Pageable, Sortable, DateRangeFilterable
