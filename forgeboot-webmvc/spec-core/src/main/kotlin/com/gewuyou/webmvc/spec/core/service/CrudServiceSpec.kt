package com.gewuyou.webmvc.spec.core.service

import com.gewuyou.forgeboot.webmvc.dto.PageResult
import com.gewuyou.webmvc.spec.core.page.QueryComponent

/**
 *CRUD服务规范
 *
 * @since 2025-07-25 11:12:32
 * @author gewuyou
 */
interface CrudServiceSpec <Entity: Any, Id: Any> {
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
     * 软删除
     *
     * 本函数用于标记实体为删除状态，而不是真正从数据库中移除
     * 这种方法可以保留历史数据，同时避免数据泄露
     *
     * @param id 实体的唯一标识符
     */
    fun softDelete(id: Id)

    /**
     * 批量软删除
     *
     * @param ids 要软删除的实体ID列表
     */
    fun softDeleteByIds(ids: List<Id>)

    /**
     * 取消软删除（恢复已删除实体）
     *
     * @param id 要恢复的实体ID
     */
    fun restore(id: Id)

    /**
     * 批量取消软删除
     *
     * @param ids 要恢复的实体ID列表
     */
    fun restoreByIds(ids: List<Id>)

    /**
     * 判断实体是否已被软删除
     *
     * @param id 实体ID
     * @return 如果是软删除状态返回 true，否则返回 false
     */
    fun isSoftDeleted(id: Id): Boolean

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
     * 查询记录总数
     *
     *
     * @return 返回记录总数
     */
    fun count(): Long

    /**
     * 分页查询实体列表
     *
     * 通过提供的查询组件进行分页数据检索，返回包含分页信息的结果对象
     *
     * @param query 查询组件，包含分页和过滤条件等信息
     * @return 返回分页结果对象，包含当前页的数据列表、总记录数等信息
     */
    fun page(query: QueryComponent): PageResult<Entity>
}