package com.gewuyou.forgeboot.webmvc.dto.request

import com.gewuyou.forgeboot.webmvc.dto.page.DateRangeFilterable
import com.gewuyou.forgeboot.webmvc.dto.page.Pageable

/**
 * 抽象日期分页请求
 *
 * 用于封装带有日期范围过滤的分页请求参数，实现分页和日期范围过滤功能的统一请求数据结构。
 *
 * @property currentPage 当前页码，默认值为1。用于标识请求的页码位置。
 * @property pageSize 每页记录数，默认值为10。用于控制分页时每页返回的数据量。
 *
 * @since 2025-07-19 10:19:23
 * @author gewuyou
 */
abstract class AbstractDatePageRequest(
    override val currentPage: Int = 1,
    override val pageSize: Int = 10,
) : Pageable, DateRangeFilterable