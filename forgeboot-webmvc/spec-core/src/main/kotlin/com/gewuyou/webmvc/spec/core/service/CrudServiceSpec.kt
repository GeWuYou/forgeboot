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

package com.gewuyou.webmvc.spec.core.service


import com.gewuyou.forgeboot.webmvc.dto.api.entities.PageResult
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
}