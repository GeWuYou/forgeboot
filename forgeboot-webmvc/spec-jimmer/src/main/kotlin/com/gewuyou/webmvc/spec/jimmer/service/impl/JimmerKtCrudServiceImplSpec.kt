/*
 *
 *  * Copyright (c) 2025
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 *
 */

package com.gewuyou.webmvc.spec.jimmer.service.impl


import com.gewuyou.forgeboot.webmvc.dto.api.entities.PageResult
import com.gewuyou.webmvc.spec.core.extension.toPageRequest
import com.gewuyou.webmvc.spec.core.extension.toPageResult
import com.gewuyou.webmvc.spec.core.page.Pageable
import com.gewuyou.webmvc.spec.core.page.QueryComponent
import com.gewuyou.webmvc.spec.jimmer.page.JimmerKFilterable
import com.gewuyou.webmvc.spec.jimmer.service.JimmerCrudServiceSpec
import org.babyfish.jimmer.View
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.spring.repository.fetchSpringPage
import org.babyfish.jimmer.sql.ast.mutation.SaveMode

/**
 *JimmerCrud Kt 服务实现规范
 *
 * @since 2025-07-30 21:30:05
 * @author gewuyou
 */
open class JimmerKtCrudServiceImplSpec<Entity : Any, Id : Any>(
    open val repository: KRepository<Entity, Id>,
) : JimmerCrudServiceSpec<Entity, Id> {
    /**
     * 根据查询条件分页查询并返回指定视图类型的分页结果
     *
     * 该函数通过泛型参数指定实体类型和视图类型，将查询结果转换为对应的视图对象返回
     *
     * @param E 实体类型参数，必须是Any的子类型
     * @param V 视图类型参数，必须是View<E>的子类型
     * @param query 查询组件，包含分页和过滤条件
     * @return PageResult<V> 返回指定视图类型的分页结果
     */
    inline fun <reified E : Any, reified V : View<E>, Q> pageWithView(
        query: Q,
    ): PageResult<V>
            where Q : Pageable, Q : JimmerKFilterable<E>, Q : QueryComponent {
        val pageable = query.toPageRequest()
        return repository.sql.createQuery(E::class) {
            where(query.getSpecification())
            select(table.fetch(V::class))
        }.fetchSpringPage(pageable).toPageResult()
    }


    /**
     * 根据ID获取实体
     *
     * @param id 实体的唯一标识符
     * @return 返回实体，如果不存在则返回null
     */
    override fun findById(id: Id): Entity? {
        return repository.findById(id).orElse(null)
    }

    /**
     * 获取所有实体列表
     *
     * @return 返回实体列表
     */
    override fun list(): List<Entity> {
        return repository.findAll()
    }

    /**
     * 保存一个实体
     *
     * @param entity 要保存的实体
     * @return 返回保存后的实体
     */
    override fun save(entity: Entity): Entity {
        return repository.save(entity, SaveMode.INSERT_ONLY)
    }

    /**
     * 更新一个实体
     *
     * @param entity 要更新的实体
     * @return 返回更新后的实体
     */
    override fun update(entity: Entity): Entity {
        return repository.save(entity, SaveMode.UPDATE_ONLY)
    }

    /**
     * 删除一个实体
     *
     * @param id 要删除的实体的ID
     */
    override fun deleteById(id: Id) {
        repository.deleteById(id)
    }

    /**
     * 批量删除实体
     *
     * @param ids 要删除的实体的ID列表
     */
    override fun deleteByIds(ids: List<Id>) {
        repository.deleteByIds(ids)
    }

    /**
     * 根据ID检查实体是否存在
     *
     * @param id 实体的ID
     * @return 如果实体存在返回true，否则返回false
     */
    override fun existsById(id: Id): Boolean {
        return repository.existsById(id)
    }

    /**
     * 批量保存实体
     *
     * @param entities 要保存的实体列表
     * @return 返回保存后的实体列表
     */
    override fun saveAll(entities: List<Entity>): List<Entity> {
        return repository.saveAll(entities)
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
        query as Pageable
        val pageable = query.toPageRequest()
        @Suppress("UNCHECKED_CAST")
        query as JimmerKFilterable<Entity>
        return repository.sql.createQuery(query.entityClass()) {
            where(query.getSpecification())
            select(table)
        }.fetchSpringPage(pageable).toPageResult()
    }

    /**
     * 保存实体对象，如果该实体不存在的话
     *
     * @param entity 需要保存的实体对象
     * @return 保存后的实体对象
     */
    override fun saveIfNotExist(entity: Entity): Entity {
        return repository.save(entity, SaveMode.INSERT_IF_ABSENT)
    }
}