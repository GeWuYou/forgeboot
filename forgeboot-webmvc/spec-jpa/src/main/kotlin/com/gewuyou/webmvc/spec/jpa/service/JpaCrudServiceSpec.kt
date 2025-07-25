package com.gewuyou.webmvc.spec.jpa.service

import com.gewuyou.forgeboot.webmvc.dto.PageResult
import com.gewuyou.webmvc.spec.core.page.QueryComponent
import com.gewuyou.webmvc.spec.core.service.CrudServiceSpec

/**
 *JPA Crud服务规范
 *
 * @since 2025-07-25 12:41:39
 * @author gewuyou
 */
interface JpaCrudServiceSpec<Entity: Any, Id: Any>: CrudServiceSpec<Entity, Id> {
    /**
     * 分页查询实体列表
     *
     * 通过提供的查询组件进行分页数据检索，返回包含分页信息的结果对象
     *
     * @param query 查询组件，包含分页和过滤条件等信息
     * @return 返回分页结果对象，包含当前页的数据列表、总记录数等信息
     */
    fun page(query: QueryComponent): PageResult<Entity>


    /**
     * 分页查询并映射结果
     *
     * 通过提供的查询组件进行分页数据检索，并使用给定的映射函数将结果转换为另一种类型
     * 适用于需要将实体转换为DTO或其他形式的场景
     *
     * @param query 查询组件，包含分页和过滤条件等信息
     * @param mapping 将实体转换为目标类型的函数
     * @return 返回分页结果对象，包含转换后的数据列表、总记录数等信息
     */
    fun <V> pageMapped(query: QueryComponent, mapping: (Entity) -> V): PageResult<V>


    /**
     * 查询符合条件的记录总数
     *
     * 根据查询组件中的过滤条件统计匹配的记录数量
     * 通常用于分页时计算总页数或显示记录总数
     *
     * @param query 查询组件，包含过滤条件等信息
     * @return 返回符合条件的记录总数
     */
    fun count(query: QueryComponent): Long
}