package com.gewuyou.forgeboot.webmvc.dto.request

import com.gewuyou.forgeboot.webmvc.dto.entities.SortCondition
import com.gewuyou.forgeboot.webmvc.dto.page.DateRangeFilterable
import com.gewuyou.forgeboot.webmvc.dto.page.Filterable
import com.gewuyou.forgeboot.webmvc.dto.page.Pageable
import com.gewuyou.forgeboot.webmvc.dto.page.Sortable

/**
 * 抽象排序日期过滤分页请求基类
 * 
 * 该抽象类为分页请求提供了基础结构，支持以下功能：
 * - 分页控制（通过Pageable接口）
 * - 排序功能（通过Sortable接口）
 * - 日期范围过滤（通过DateRangeFilterable接口）
 * - 通用数据过滤（通过Filterable<T,E>接口）
 *
 * @property currentPage 当前页码，从1开始计数，默认值为1
 * @property pageSize 每页记录数，默认值为10
 * 
 * @since 2025-07-19 14:50:36
 * @author gewuyou
 */
abstract class AbstractSortedDateFilterPageRequest<T,E>(
    override val currentPage: Int = 1,
    override val pageSize: Int = 10,
    override val sortConditions: List<SortCondition> = mutableListOf()
) : Pageable, Sortable, DateRangeFilterable, Filterable<T,E>