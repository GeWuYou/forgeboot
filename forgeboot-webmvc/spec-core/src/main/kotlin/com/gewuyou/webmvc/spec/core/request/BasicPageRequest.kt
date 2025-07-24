package com.gewuyou.webmvc.spec.core.request

import com.gewuyou.webmvc.spec.core.page.Pageable


/**
 * 基本分页请求数据类
 *
 * 该类用于封装分页请求的基础信息，包括当前页码和每页记录数。
 * 通过实现 Pageable 接口，支持与分页处理相关的操作。
 *
 * @property currentPage 当前页码，默认值为 1
 * @property pageSize 每页记录数，默认值为 10
 *
 * @since 2025-07-19 09:12:22
 * @author gewuyou
 */
data class BasicPageRequest(
    override val currentPage: Int = 1,
    override val pageSize: Int = 10
) : Pageable