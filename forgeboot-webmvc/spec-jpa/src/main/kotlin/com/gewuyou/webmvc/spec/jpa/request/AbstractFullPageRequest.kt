package com.gewuyou.webmvc.spec.jpa.request


import com.gewuyou.webmvc.spec.core.page.DateRangeFilterable
import com.gewuyou.webmvc.spec.core.page.KeywordSearchable
import com.gewuyou.webmvc.spec.core.page.Pageable
import com.gewuyou.webmvc.spec.core.page.Sortable
import com.gewuyou.webmvc.spec.core.page.StatusFilterable
import com.gewuyou.webmvc.spec.jpa.page.JpaFilterable

/**
 * 抽象分页请求类，用于封装分页查询的通用参数
 *
 * 该类实现了多种分页相关接口，支持分页、排序、过滤、关键词搜索、状态条件筛选和日期范围过滤功能
 *
 * @param T 泛型类型参数，表示自定义过滤条件的数据类型
 *
 * @property currentPage 当前页码，默认值为1
 * @property pageSize 每页记录数，默认值为10
 * @property keyword 关键词搜索内容，可为空
 * @property filter 自定义过滤条件，可为空，类型为泛型T
 *
 * @author gewuyou
 * @since 2025-07-19 10:25:55
 */
abstract class AbstractFullPageRequest<T,E>(
    override val currentPage: Int = 1,
    override val pageSize: Int = 10,
    override val keyword: String? = null,
    override val filter: T? = null,
) : Pageable,
    Sortable,
    KeywordSearchable,
    JpaFilterable<T, E>,
    DateRangeFilterable,
    StatusFilterable