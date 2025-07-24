package com.gewuyou.webmvc.spec.jpa.page

import com.gewuyou.webmvc.spec.core.page.StatusFilterable
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root


/**
 * 启用状态可过滤接口，用于支持启用/禁用状态的过滤功能。
 *
 *
 * @since 2025-07-19 09:00:42
 * @author gewuyou
 */
interface JpaStatusFilterable : StatusFilterable{
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