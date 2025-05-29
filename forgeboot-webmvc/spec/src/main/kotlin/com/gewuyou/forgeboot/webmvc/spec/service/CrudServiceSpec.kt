package com.gewuyou.forgeboot.webmvc.spec.service

import com.gewuyou.forgeboot.webmvc.dto.PageResult
import com.gewuyou.forgeboot.webmvc.dto.request.PageQueryReq

/**
 * CRUD 服务规范
 *
 * 定义了对实体进行基本操作的服务接口，包括增删查改和分页查询等功能
 *
 * @param Entity 实体类型
 * @param Id 实体标识符类型
 * @param Filter 查询过滤器类型
 * @since 2025-05-29 20:18:22
 * @author gewuyou
 */
interface CrudServiceSpec<Entity: Any, Id: Any, Filter: Any> {
    /**
     * 根据ID获取实体
     *
     * @param id 实体的唯一标识符
     * @return 返回实体，如果不存在则返回null
     */
    fun findById(id: Id): Entity?

    /**
     * 获取所有实体列表
     *
     * @return 返回实体列表
     */
    fun list(): List<Entity>

    /**
     * 保存一个实体
     *
     * @param entity 要保存的实体
     * @return 返回保存后的实体
     */
    fun save(entity: Entity): Entity

    /**
     * 更新一个实体
     *
     * @param entity 要更新的实体
     * @return 返回更新后的实体
     */
    fun update(entity: Entity): Entity

    /**
     * 删除一个实体
     *
     * @param id 要删除的实体的ID
     */
    fun delete(id: Id)

    /**
     * 批量删除实体
     *
     * @param ids 要删除的实体的ID列表
     */
    fun delete(ids: List<Id>)

    /**
     * 删除一个实体
     *
     * @param entity 要删除的实体
     */
    fun delete(entity: Entity)

    /**
     * 批量删除实体
     *
     * @param entities 要删除的实体列表
     */
    fun delete(entities: List<Entity>)

    /**
     * 根据ID检查实体是否存在
     *
     * @param id 实体的ID
     * @return 如果实体存在返回true，否则返回false
     */
    fun existsById(id: Id): Boolean


    /**
     * 批量保存实体
     *
     * @param entities 要保存的实体列表
     * @return 返回保存后的实体列表
     */
    fun saveAll(entities: List<Entity>): List<Entity>

    /**
     * 分页查询并映射结果
     *
     * @param query 分页查询请求，包含查询参数和分页信息
     * @return 返回映射后的分页结果
     */
    fun page(query: PageQueryReq<Filter>): PageResult<Entity>

    /**
     * 分页查询并使用给定函数映射结果
     *
     * @param query 分页查询请求，包含查询参数和分页信息
     * @param mapping 映射函数，用于将实体映射为其他类型
     * @param <V> 映射后的类型
     * @return 返回映射后的分页结果
     */
    fun <V> pageMapped(query: PageQueryReq<Filter>, mapping: (Entity) -> V): PageResult<V>

    /**
     * 根据过滤条件统计实体数量
     *
     * @param filter 查询过滤器
     * @return 返回满足条件的实体数量
     */
    fun count(filter: Filter): Long
}
