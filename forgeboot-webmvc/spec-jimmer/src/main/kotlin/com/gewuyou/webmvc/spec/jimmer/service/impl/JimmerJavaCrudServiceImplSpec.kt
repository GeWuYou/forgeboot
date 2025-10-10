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

import com.gewuyou.webmvc.spec.jimmer.service.JimmerCrudServiceSpec
import org.babyfish.jimmer.spring.repo.support.AbstractJavaRepository
import org.babyfish.jimmer.sql.ast.mutation.SaveMode

/**
 *Jimmer Java Crud 服务规范
 *
 * @since 2025-08-17 22:54:33
 * @author gewuyou
 */
abstract class JimmerJavaCrudServiceImplSpec<Entity : Any, Id : Any>(
    open val repository: AbstractJavaRepository<Entity, Id>,
) : JimmerCrudServiceSpec<Entity, Id> {
    /**
     * 保存实体对象，如果该实体不存在的话
     *
     * @param entity 需要保存的实体对象
     * @return 保存后的实体对象
     */
    override fun saveIfNotExist(entity: Entity): Entity {
        return repository.save(entity, SaveMode.INSERT_IF_ABSENT).modifiedEntity
    }

    /**
     * 根据ID获取实体
     *
     * @param id 实体的唯一标识符
     * @return 返回实体，如果不存在则返回null
     */
    override fun findById(id: Id): Entity? {
        return repository.findById(id)
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
        return repository.save(entity, SaveMode.INSERT_ONLY).modifiedEntity
    }

    /**
     * 更新一个实体
     *
     * @param entity 要更新的实体
     * @return 返回更新后的实体
     */
    override fun update(entity: Entity): Entity {
        return repository.save(entity, SaveMode.UPDATE_ONLY).modifiedEntity
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
        return repository.findById(id) != null
    }

    /**
     * 批量保存实体
     *
     * @param entities 要保存的实体列表
     * @return 返回保存后的实体列表
     */
    override fun saveAll(entities: List<Entity>): List<Entity> {
        return repository.saveEntities(entities).items.map { it.modifiedEntity }
    }
}