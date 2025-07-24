package com.gewuyou.webmvc.spec.core.page

/**
 * 关键字可搜索接口，用于支持包含关键字搜索条件的数据结构
 *
 * 该接口定义了关键字搜索所需的基础结构，包括关键字本身和需要进行模糊匹配的字段列表。
 * 实现该接口的数据结构可以在查询中支持基于关键字的模糊匹配功能。
 *
 * @since 2025-07-19 08:56:31
 * @author gewuyou
 */
interface KeywordSearchable : QueryComponent {
    /**
     * 关键字，用于模糊匹配。
     *
     * 如果值为 null 或空字符串，则表示不需要进行关键字过滤。
     */
    val keyword: String?

    /**
     * 获取需要被关键字模糊匹配的字段列表。
     *
     * 返回的字段列表用于构建模糊查询条件，每个字段将与关键字进行模糊匹配。
     * 字段名称应为数据结构中的实际字段名，且应支持模糊匹配操作。
     *
     * @return 需要进行模糊匹配的字段列表，不为 null。
     */
    fun keywordSearchFields(): List<String>
}