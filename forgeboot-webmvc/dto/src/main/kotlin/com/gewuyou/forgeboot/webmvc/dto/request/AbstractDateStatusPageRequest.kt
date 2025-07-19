package com.gewuyou.forgeboot.webmvc.dto.request

import com.gewuyou.forgeboot.webmvc.dto.page.DateRangeFilterable
import com.gewuyou.forgeboot.webmvc.dto.page.Pageable
import com.gewuyou.forgeboot.webmvc.dto.page.StatusFilterable

/**
 * 日期状态页面请求
 * 用于封装包含分页、日期范围和状态过滤条件的复合请求参数
 *
 * @property currentPage 当前页码，从1开始计数的分页参数
 * @property pageSize 每页记录数量，控制分页大小
 * @property statusConditions 状态条件映射，键值对形式表示的状态过滤条件
 *
 * @since 2025-07-19 09:15:12
 * @author gewuyou
 */
abstract class AbstractDateStatusPageRequest(
    override val currentPage: Int = 1,
    override val pageSize: Int = 10,
) : Pageable, DateRangeFilterable, StatusFilterable