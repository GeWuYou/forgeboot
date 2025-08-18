package com.gewuyou.webmvc.spec.jpa.service.impl

import com.gewuyou.forgeboot.webmvc.dto.PageResult
import com.gewuyou.webmvc.spec.core.extension.map
import com.gewuyou.webmvc.spec.core.extension.toPageResult
import com.gewuyou.webmvc.spec.core.page.QueryComponent
import com.gewuyou.webmvc.spec.jpa.extension.toJpaQuery
import com.gewuyou.webmvc.spec.jpa.extension.toSpecification
import com.gewuyou.webmvc.spec.jpa.repository.JpaCrudRepositorySpec
import com.gewuyou.webmvc.spec.jpa.service.JpaCrudServiceSpec

/**
 * Jpa CRUD 服务实现的抽象类，提供通用的数据访问操作。
 *
 * @since 2025-05-29 20:37:27
 * @author gewuyou
 */
abstract class JpaCrudServiceImplSpec<Entity : Any, Id : Any>(
    private val repository: JpaCrudRepositorySpec<Entity, Id>,
) : JpaCrudServiceSpec<Entity, Id> {

    /**
     * 根据实体的唯一标识符查找实体对象。
     *
     * @param id 实体的唯一标识符
     * @return 如果存在则返回对应的实体对象，否则返回 null
     */
    override fun findById(id: Id): Entity? {
        return repository.findById(id).orElse(null)
    }

    /**
     * 获取所有实体列表。
     *
     * @return 返回实体列表
     */
    override fun list(): List<Entity> {
        return repository.findAll()
    }

    /**
     * 保存一个实体对象。
     *
     * @param entity 要保存的实体对象
     * @return 返回保存后的实体对象
     */
    override fun save(entity: Entity): Entity {
        return repository.save(entity)
    }

    /**
     * 根据实体 ID 删除指定的实体。
     *
     * @param id 要删除的实体的唯一标识符
     */
    override fun deleteById(id: Id) {
        repository.deleteById(id)
    }

    /**
     * 批量删除多个实体（根据 ID 列表）。
     *
     * @param ids 要删除的实体 ID 列表
     */
    override fun deleteByIds(ids: List<Id>) {
        repository.deleteAllById(ids)
    }


    /**
     * 批量删除多个实体。
     *
     * @param entities 要删除的实体列表
     */
    override fun deleteByAll(entities: List<Entity>) {
        repository.deleteAll(entities)
    }

    /**
     * 将更新实体的非 null 字段合并到已有持久化对象上。
     * 此方法通常由 MapStruct 等映射工具实现。
     *
     * @param entity  包含更新数据的实体对象
     * @param existing 数据库中已存在的实体对象
     * @return 返回合并后的实体对象
     */
    protected abstract fun merge(entity: Entity, existing: Entity): Entity

    /**
     * 提取实体对象中的唯一标识符。
     *
     * @param entity 实体对象
     * @return 返回实体的唯一标识符
     */
    protected abstract fun extractId(entity: Entity): Id

    /**
     * 更新一个实体对象。
     *
     * @param entity 包含更新数据的实体对象
     * @return 返回更新后的实体对象
     */
    override fun update(entity: Entity): Entity {
        val existing = repository.findById(extractId(entity))
            .orElseThrow { IllegalArgumentException("Entity not found") }

        val merged = merge(entity, existing)
        return repository.save(merged)
    }

    /**
     * 分页查询实体列表
     *
     * 通过提供的查询组件进行分页数据检索，返回包含分页信息的结果对象
     *
     * @param query 查询组件，包含分页和过滤条件等信息
     * @return 返回分页结果对象，包含当前页的数据列表、总记录数等信息
     */
    override fun page(query: QueryComponent): PageResult<Entity> {
        val (specification, pageable) = query.toJpaQuery<Entity>()
        return repository.findAll(specification, pageable).toPageResult()
    }

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
    override fun <V> pageMapped(
        query: QueryComponent,
        mapping: (Entity) -> V,
    ): PageResult<V> {
        val (specification, pageable) = query.toJpaQuery<Entity>()
        return repository.findAll(specification, pageable)
            .toPageResult()
            .map(mapping)
    }

    /**
     * 查询符合条件的记录总数
     *
     * 根据查询组件中的过滤条件统计匹配的记录数量
     * 通常用于分页时计算总页数或显示记录总数
     *
     * @param query 查询组件，包含过滤条件等信息
     * @return 返回符合条件的记录总数
     */
    override fun count(query: QueryComponent): Long {
        val specification = query.toSpecification<Entity>()
        return repository.count(specification)
    }

    /**
     * 检查具有指定 ID 的实体是否存在。
     *
     * @param id 实体的唯一标识符
     * @return 如果实体存在返回 true，否则返回 false
     */
    override fun existsById(id: Id): Boolean {
        return repository.existsById(id)
    }

    /**
     * 批量保存多个实体对象。
     *
     * @param entities 要保存的实体列表
     * @return 返回保存后的实体列表
     */
    override fun saveAll(entities: List<Entity>): List<Entity> {
        return repository.saveAll(entities)
    }

    /**
     * 标记实体为软删除状态。
     *
     * 此方法应在子类中实现，用于定义如何将实体标记为已删除（例如设置一个 deleted 字段）。
     * 软删除不会从数据库中物理移除记录，而是将其标记为已删除状态，以便保留历史数据。
     *
     * @param entity 实体对象，表示要标记为删除状态的对象
     */
    protected abstract fun setDeleted(entity: Entity)

    /**
     * 将实体标记为已恢复状态。
     *
     * 此方法应在子类中实现，用于定义如何将实体从软删除状态恢复为正常状态（例如清除 deleted 字段或设置为 false）。
     * 该机制允许在不物理删除数据的情况下，灵活地控制记录的可见性与状态。
     *
     * @param entity 实体对象，表示要恢复的实体
     */
    protected abstract fun setRestored(entity: Entity)

    /**
     * 判断实体是否已被软删除
     *
     * @param entity 实体对象，用于检查其删除状态
     * @return 如果实体已被标记为软删除状态返回 true，否则返回 false
     */
    protected abstract fun isSoftDeletedByEntity(entity: Entity): Boolean

    /**
     * 执行软删除操作。
     *
     * 该方法会根据提供的 ID 查找实体。如果找到该实体，则调用 [setDeleted] 方法将其标记为删除状态，
     * 然后通过 [update] 方法保存更改；如果没有找到实体，则记录一条错误日志。
     *
     * 软删除机制可以保留历史数据，同时避免敏感信息的直接删除，确保数据可追溯且安全。
     *
     * @param id 实体的唯一标识符，用于查找需要删除的实体
     */
    override fun softDelete(id: Id) {
        val exist: Entity? = findById(id)
        exist?.let {
            setDeleted(it)
            update(it)
        }
    }

    /**
     * 查询记录总数
     *
     *
     * @return 返回记录总数
     */
    override fun count(): Long {
       return repository.count()
    }

    /**
     * 批量软删除
     *
     * @param ids 要软删除的实体ID列表
     */
    override fun softDeleteByIds(ids: List<Id>) {
        ids.forEach {
            softDelete( it)
        }
    }

    /**
     * 取消软删除（恢复已删除实体）
     *
     * @param id 要恢复的实体ID
     */
    override fun restore(id: Id) {
        val exist: Entity? = findById(id)
        exist?.let {
            setRestored(exist)
            update(it)
        }
    }

    /**
     * 批量取消软删除
     *
     * @param ids 要恢复的实体ID列表
     */
    override fun restoreByIds(ids: List<Id>) {
        ids.forEach {
            restore(it)
        }
    }

    /**
     * 判断实体是否已被软删除
     *
     * @param id 实体ID
     * @return 如果是软删除状态返回 true，否则返回 false
     */
    override fun isSoftDeleted(id: Id): Boolean {
        val entity = findById(id)?:return false
        return isSoftDeletedByEntity(entity)
    }
}