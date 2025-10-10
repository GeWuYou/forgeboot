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

package com.gewuyou.webmvc.spec.jimmer.service

import com.gewuyou.webmvc.spec.core.service.CrudServiceSpec

/**
 *Jimmer Crud服务规范
 *
 * @since 2025-07-30 21:16:56
 * @author gewuyou
 */
interface JimmerCrudServiceSpec<Entity : Any, Id : Any> : CrudServiceSpec<Entity, Id> {
    /**
     * 保存实体对象，如果该实体不存在的话
     *
     * @param entity 需要保存的实体对象
     * @return 保存后的实体对象
     */
    fun saveIfNotExist(entity: Entity): Entity

}