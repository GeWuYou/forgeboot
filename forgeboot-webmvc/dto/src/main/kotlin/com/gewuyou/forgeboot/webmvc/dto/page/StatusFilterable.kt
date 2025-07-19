package com.gewuyou.forgeboot.webmvc.dto.page

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root

/**
 * 启用状态可过滤接口，用于支持启用/禁用状态的过滤功能。
 *
 * 此接口定义了构建状态过滤条件的方法，适用于基于 JPA Criteria API 的查询构建。
 * 实现该接口的类可通过 [buildStatusPredicates]方法提供具体过滤逻辑。
 *
 * @since 2025-07-19 09:00:42
 * @author gewuyou
 */
interface StatusFilterable : QueryComponent {
    /**
     * 构建与启用状态相关的查询条件列表。
     *
     * 该方法用于生成一个 Predicate 列表，用于 JPA Criteria 查询中对实体的状态字段进行过滤。
     * 通常用于支持启用/禁用状态的动态查询场景。
     *
     * @param <T> 查询所涉及的实体类型
     * @param root Criteria 查询的根对象，用于访问实体属性
     * @param cb CriteriaBuilder 实例，用于构建查询条件
     * @return 构建完成的 Predicate 条件列表
     */
    fun <T> buildStatusPredicates(root: Root<T>, cb: CriteriaBuilder): List<Predicate>
}