package com.gewuyou.webmvc.spec.jpa.service.impl

import com.gewuyou.webmvc.spec.jpa.repository.JpaCrudRepositorySpec

/**
 *简单的JPA Crud服务实现规范,请注意该抽象实现类不实现软删除
 *
 * @since 2025-07-25 14:24:19
 * @author gewuyou
 */
abstract class SimpleJpaCrudServiceImplSpec<Entity : Any, Id : Any>(
    private val repository: JpaCrudRepositorySpec<Entity, Id>,
): JpaCrudServiceImplSpec<Entity,Id>(repository) {
    /**
     * 标记实体为软删除状态。
     *
     * 此方法应在子类中实现，用于定义如何将实体标记为已删除（例如设置一个 deleted 字段）。
     * 软删除不会从数据库中物理移除记录，而是将其标记为已删除状态，以便保留历史数据。
     *
     * @param entity 实体对象，表示要标记为删除状态的对象
     */
    override fun setDeleted(entity: Entity) {
        // 不实现
    }

    /**
     * 将实体标记为已恢复状态。
     *
     * 此方法应在子类中实现，用于定义如何将实体从软删除状态恢复为正常状态（例如清除 deleted 字段或设置为 false）。
     * 该机制允许在不物理删除数据的情况下，灵活地控制记录的可见性与状态。
     *
     * @param entity 实体对象，表示要恢复的实体
     */
    override fun setRestored(entity: Entity) {
        // 不实现
    }

    /**
     * 判断实体是否已被软删除
     *
     * @param entity 实体对象，用于检查其删除状态
     * @return 如果实体已被标记为软删除状态返回 true，否则返回 false
     */
    override fun isSoftDeletedByEntity(entity: Entity): Boolean {
        return false
    }
}