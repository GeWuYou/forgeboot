package com.gewuyou.webmvc.spec.jpa.request


import com.gewuyou.webmvc.spec.core.enums.SortDirection
import com.gewuyou.webmvc.spec.core.page.Pageable
import com.gewuyou.webmvc.spec.jpa.page.JpaFilterable


/**
 * 抽象过滤的分页请求
 *
 * 该类实现了分页和过滤功能的请求数据封装，用于构建带过滤条件的分页查询。
 *
 * @param currentPage 当前页码，从1开始计数，默认为1
 * @param pageSize 每页显示的数据数量，默认为10
 * @param filter 过滤条件对象，泛型类型T的可空实例，默认为null
 *
 * @since 2025-07-19 09:16:36
 * @author gewuyou
 */
abstract class AbstractFilteredPageRequest<T, E>(
    override val currentPage: Int = 1,
    override val pageSize: Int = 10,
    override val filter: T? = null,
    sortDirection: SortDirection
) : Pageable, JpaFilterable<T, E>