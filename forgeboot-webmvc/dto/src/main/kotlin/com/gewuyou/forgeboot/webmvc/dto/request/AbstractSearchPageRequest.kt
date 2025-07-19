package com.gewuyou.forgeboot.webmvc.dto.request

import com.gewuyou.forgeboot.webmvc.dto.page.KeywordSearchable
import com.gewuyou.forgeboot.webmvc.dto.page.Pageable

/**
 * 抽象搜索分页请求类
 *
 * 该类用于封装分页搜索请求的基本参数，包括当前页码、页大小和关键字。
 * 通过实现 [Pageable] 和 [KeywordSearchable] 接口，提供分页和关键字搜索功能。
 *
 * @property currentPage 当前页码，默认为 1
 * @property pageSize 每页条目数，默认为 10
 * @property keyword 搜索关键字，可为空，默认为 null
 *
 * @since 2025-07-19 09:14:28
 * @author gewuyou
 */
abstract class AbstractSearchPageRequest(
    override val currentPage: Int = 1,
    override val pageSize: Int = 10,
    override val keyword: String? = null
) : Pageable, KeywordSearchable