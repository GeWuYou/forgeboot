package com.gewuyou.webmvc.spec.jpa.page

import com.gewuyou.webmvc.spec.core.page.Filterable
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root

/**
 *JPA可过滤接口
 *
 * @since 2025-07-24 21:40:33
 * @author gewuyou
 */
interface JpaFilterable<FilterType, EntityType> : Filterable<FilterType> {
    /**
     * 构建JPA查询条件规范
     *
     * 根据当前过滤条件构建JPA Criteria查询所需的谓词列表，用于动态生成查询条件。
     * 这些谓词将被用于构建最终的JPA查询语句。
     *
     * @param root 查询根对象，用于访问实体属性
     * @param cb CriteriaBuilder对象，用于创建查询条件谓词
     * @return 包含查询条件谓词的列表
     */
    fun buildSpecification(root: Root<EntityType>, cb: CriteriaBuilder): List<Predicate>
}