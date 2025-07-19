package com.gewuyou.forgeboot.webmvc.spec.service

import com.gewuyou.forgeboot.webmvc.dto.PageResult
import com.gewuyou.forgeboot.webmvc.dto.page.QueryComponent

/**
 * CRUD 服务规范
 *
 * 定义了对实体进行基本操作的服务接口，包括增删查改和分页查询等功能
 *
 * @param Entity 实体类型
 * @param Id 实体标识符类型
 * @since 2025-05-29 20:18:22
 * @author gewuyou
 */
interface CrudServiceSpec<Entity: Any, Id: Any> {
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
    fun deleteById(id: Id)

    /**
     * 批量删除实体
     *
     * @param ids 要删除的实体的ID列表
     */
    fun deleteByIds(ids: List<Id>)

    /**
     * 删除一个实体
     *
     * @param entity 要删除的实体
     */
    fun deleteByOne(entity: Entity)

    /**
     * 批量删除实体
     *
     * @param entities 要删除的实体列表
     */
    fun deleteByAll(entities: List<Entity>)

    /**
     * 软删除
     *
     * 本函数用于标记实体为删除状态，而不是真正从数据库中移除
     * 这种方法可以保留历史数据，同时避免数据泄露
     *
     * @param id 实体的唯一标识符
     */
    fun softDelete(id: Id)

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
