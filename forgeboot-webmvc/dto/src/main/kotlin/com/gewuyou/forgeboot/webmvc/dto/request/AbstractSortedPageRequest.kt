package com.gewuyou.forgeboot.webmvc.dto.request

import com.gewuyou.forgeboot.webmvc.dto.page.Pageable
import com.gewuyou.forgeboot.webmvc.dto.page.Sortable

/**
 * 抽象排序分页请求数据类
 *
 * 该类用于封装带有排序功能的分页请求参数，包含当前页码、每页大小、默认排序字段、
 * 排序方向以及扩展的排序条件集合。通过实现 [Pageable] 和 [Sortable] 接口，
 * 提供标准化的分页与排序属性。
 *
 * @property currentPage 当前页码，从1开始，默认值为1
 * @property pageSize 每页记录数，默认值为10
 *
 * @since 2025-07-19 09:13:19
 * @author gewuyou
 */
abstract class AbstractSortedPageRequest(
    override val currentPage: Int = 1,
    override val pageSize: Int = 10,
) : Pageable, Sortable