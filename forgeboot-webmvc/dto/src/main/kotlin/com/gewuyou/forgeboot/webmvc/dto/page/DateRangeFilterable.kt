package com.gewuyou.forgeboot.webmvc.dto.page

import com.gewuyou.forgeboot.webmvc.dto.entities.DateRangeCondition

/**
 * 日期范围可过滤接口
 * 用于定义支持日期范围过滤的对象结构，包含开始时间和结束时间属性
 *
 * 该接口继承自QueryComponent，用于构建支持多日期范围过滤条件的查询组件。
 * 实现该接口的类需要提供具体的日期范围条件集合。
 *
 * @since 2025-07-19 08:59:44
 * @author gewuyou
 */
fun interface DateRangeFilterable : QueryComponent {
    /**
     * 获取所有日期范围条件集合
     *
     * 该方法返回一个包含所有日期范围条件的列表，每个元素都是一个DateRangeCondition对象，
     * 表示一个独立的日期范围过滤条件。
     *
     * @return List<DateRangeCondition> 类型的列表，包含所有的日期范围条件
     */
    fun dateRanges(): List<DateRangeCondition>
}