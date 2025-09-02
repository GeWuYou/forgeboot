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

package com.gewuyou.webmvc.spec.jpa.service


import com.gewuyou.forgeboot.webmvc.dto.api.entities.PageResult
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


    /**
     * 查询记录总数
     *
     *
     * @return 返回记录总数
     */
    fun count(): Long


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
}