package com.gewuyou.forgeboot.webmvc.dto.page

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root

/**
 * 可过滤接口，用于支持泛型类型的过滤操作
 *
 * 该接口定义了过滤功能的基本契约，允许实现类根据给定的过滤条件构建查询规范。
 * 主要用于与 JPA Criteria API 集成，以构建动态查询条件。
 *
 * @param <FilterType> 过滤条件的类型，通常是一个包含过滤参数的数据传输对象（DTO）。
 * @param <EntityType> 被查询的实体类型，通常对应数据库中的某个实体类。
 *
 * @since 2025-07-19 08:56:56
 * @author gewuyou
 */
interface Filterable<FilterType, EntityType> : QueryComponent {
    /**
     * 获取当前的过滤条件对象。
     *
     * @return 返回一个可空的 FilterType 实例，表示当前的过滤条件。
     */
    val filter: FilterType?

    /**
     * 构建查询条件的谓词列表。
     *
     * 此方法根据当前的过滤条件，使用给定的 CriteriaBuilder 创建一组 Predicate 对象，
     * 这些谓词可以用于构建最终查询语句。
     *
     * @param root 代表查询的根实体，用于访问实体的属性。
     * @param cb CriteriaBuilder 实例，用于创建查询条件。
     * @return 返回一个包含查询条件的 Predicate 列表。
     */
    fun buildSpecification(root: Root<EntityType>, cb: CriteriaBuilder): List<Predicate>
}
