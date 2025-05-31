package com.gewuyou.forgeboot.webmvc.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.gewuyou.forgeboot.webmvc.dto.SortCondition
import com.gewuyou.forgeboot.webmvc.dto.enums.SortDirection
import jakarta.validation.constraints.Min
import java.time.Instant

/**
 * 分页查询条件请求实体类
 *
 * 用于通用分页、排序、关键字搜索、日期范围与状态过滤。
 * 支持自定义泛型过滤器实体。
 *
 * @author gewuyou
 * @since 2025-01-16 16:01:12
 */
@JsonIgnoreProperties(ignoreUnknown = true)
open class PageQueryReq<T> {

    /**
     * 当前页码(默认1)
     */
    @field:Min(1)
    var currentPage: Int = 1

    /**
     * 每页条数(默认10)
     */
    @field:Min(1)
    var pageSize: Int = 10

    /**
     * 排序字段(单一字段)
     */
    var sortBy: String = "createdAt"

    /**
     * 排序方向(单一字段，ASC或DESC)
     */
    var sortDirection: SortDirection = SortDirection.DESC

    /**
     * 排序条件实体类（支持多字段排序）
     */
    var sortConditions: MutableList<SortCondition> = mutableListOf()

    /**
     * 关键字搜索，常用于模糊查询
     */
    var keyword: String? = null

    /**
     * 自定义过滤条件实体类
     */
    var filter: T? = null

    /**
     * 开始日期
     */
    var startDate: Instant? = null

    /**
     * 结束日期
     */
    var endDate: Instant? = null
    /**
     * 是否启用
     */
    var enabled: Boolean? = null

    /**
     * 是否删除
     */
    var deleted: Boolean = false

    /**
     * 计算分页偏移量
     */
    fun getOffset(): Int = (currentPage - 1) * pageSize

    /**
     * 校验日期范围是否合法（开始时间不能晚于结束时间）
     */
    fun isDateRangeValid(): Boolean {
        return startDate == null || endDate == null || !startDate!!.isAfter(endDate)
    }
}