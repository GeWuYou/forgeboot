package com.gewuyou.webmvc.spec.core.page

/**
 * 可分页接口，用于支持分页功能的类实现
 *
 * @since 2025-07-19 08:52:58
 * @author gewuyou
 */
interface Pageable : QueryComponent {
    /**
     * 当前页码，从1开始计数
     */
    val currentPage: Int

    /**
     * 每页显示的数据条数
     */
    val pageSize: Int

    /**
     * 计算并返回当前页的起始偏移量
     *
     * @return 返回计算后的偏移量，用于数据库查询等场景
     */
    fun getOffset(): Int = (currentPage - 1) * pageSize
}