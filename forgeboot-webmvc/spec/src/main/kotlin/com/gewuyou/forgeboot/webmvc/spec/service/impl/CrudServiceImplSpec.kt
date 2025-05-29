package com.gewuyou.forgeboot.webmvc.spec.service.impl

import com.gewuyou.forgeboot.webmvc.dto.PageResult
import com.gewuyou.forgeboot.webmvc.dto.extension.map
import com.gewuyou.forgeboot.webmvc.dto.extension.toPageResult
import com.gewuyou.forgeboot.webmvc.dto.extension.toPageable
import com.gewuyou.forgeboot.webmvc.dto.request.PageQueryReq
import com.gewuyou.forgeboot.webmvc.spec.repository.CrudRepositorySpec
import com.gewuyou.forgeboot.webmvc.spec.service.CrudServiceSpec
import org.springframework.data.jpa.domain.Specification


/**
 * CRUD 服务实现的抽象类，提供通用的数据访问操作。
 *
 * @since 2025-05-29 20:37:27
 * @author gewuyou
 */
abstract class CrudServiceImplSpec<Entity : Any, Id : Any, Filter : Any>(
    private val repository: CrudRepositorySpec<Entity, Id>,
) : CrudServiceSpec<Entity, Id, Filter> {

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
     * 执行分页查询并返回原始实体的分页结果。
     *
     * @param query 分页查询请求，包含过滤条件和分页信息
     * @return 返回实体类型的分页结果
     */
    override fun page(query: PageQueryReq<Filter>): PageResult<Entity> {
        return repository
            .findAll(
                buildSpecification(query),
                query.toPageable()
            )
            .toPageResult()
    }

    /**
     * 构建 JPA Specification 查询条件。
     *
     * @param query 包含过滤条件的分页请求
     * @return 返回构建好的 Specification 查询条件
     */
    protected abstract fun buildSpecification(query: PageQueryReq<Filter>): Specification<Entity>

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
    override fun delete(id: Id) {
        repository.deleteById(id)
    }

    /**
     * 批量删除多个实体（根据 ID 列表）。
     *
     * @param ids 要删除的实体 ID 列表
     */
    override fun delete(ids: List<Id>) {
        repository.deleteAllById(ids)
    }

    /**
     * 删除指定的单个实体。
     *
     * @param entity 要删除的实体对象
     */
    override fun delete(entity: Entity) {
        repository.delete(entity)
    }

    /**
     * 批量删除多个实体。
     *
     * @param entities 要删除的实体列表
     */
    override fun delete(entities: List<Entity>) {
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
     * 执行分页查询并将结果使用给定的映射函数转换为其他类型。
     *
     * @param query 分页查询请求，包含查询参数和分页信息
     * @param mapping 映射函数，用于将实体映射为其他类型
     * @param <V> 映射后的目标类型
     * @return 返回映射后的分页结果
     */
    override fun <V> pageMapped(
        query: PageQueryReq<Filter>,
        mapping: (Entity) -> V,
    ): PageResult<V> {
        val page = repository.findAll(
            buildSpecification(query),
            query.toPageable()
        )
        return page.toPageResult().map(mapping)
    }

    /**
     * 根据给定的过滤条件统计实体数量。
     *
     * @param filter 查询过滤器
     * @return 返回满足条件的实体数量
     */
    override fun count(filter: Filter): Long {
        return repository.count(buildSpecification(PageQueryReq<Filter>().apply { this.filter = filter }))
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
}