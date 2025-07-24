package com.gewuyou.webmvc.spec.jpa.request


import com.gewuyou.webmvc.spec.core.page.KeywordSearchable
import com.gewuyou.webmvc.spec.core.page.Pageable
import com.gewuyou.webmvc.spec.jpa.page.JpaFilterable

/**
 * 抽象搜索过滤分页请求
 *
 * 该类实现了分页、关键词搜索和过滤功能的通用请求对象。
 * 作为基类用于构建具有统一分页和搜索能力的请求数据结构。
 *
 * @param currentPage 当前页码，从1开始，默认为1
 * @param pageSize 每页记录数，默认为10
 * @param keyword 关键词搜索内容，可为空
 * @param filter 过滤条件对象，泛型类型，可为空
 *
 * @since 2025-07-19 10:22:41
 * @author gewuyou
 */
abstract class AbstractSearchFilterPageRequest<T, E>(
    override val currentPage: Int = 1,
    override val pageSize: Int = 10,
    override val keyword: String? = null,
    override val filter: T? = null,
) : Pageable, KeywordSearchable, JpaFilterable<T, E>
