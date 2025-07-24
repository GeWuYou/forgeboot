package com.gewuyou.webmvc.spec.core.entities

import java.time.Instant

/**
 * 日期范围条件
 *
 * 表示一个用于描述日期范围的条件类，包含字段名、起始日期和结束日期。
 *
 * @property fieldName 用于存储该日期范围所对应字段的名称
 * @property startDate 日期范围的开始时间，可为空，默认值为 null
 * @property endDate 日期范围的结束时间，可为空，默认值为 null
 *
 * @since 2025-07-19 12:11:53
 * @author gewuyou
 */
data class DateRangeCondition(
    val fieldName: String,
    val startDate: Instant? = null,
    val endDate: Instant? = null
) {
    /**
     * 验证日期范围的有效性
     *
     * 检查日期范围是否有效。当起始日期和结束日期都存在时，要求起始日期不能晚于结束日期。
     *
     * @return 如果日期范围有效则返回 true，否则返回 false
     */
    fun isValid(): Boolean = startDate == null || endDate == null || !startDate.isAfter(endDate)
}