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

package com.gewuyou.forgeboot.webmvc.dto.api.provider

import com.gewuyou.forgeboot.i18n.api.InfoLike

/**
 * 信息提供商接
 *
 * 用于提供和管理InfoLike对象的接口，允许通过名称获取特定的信息对象，
 * 或获取所有可用的信息对象集合。该接口提供了默认实现，子类可以选择性重写方法。
 *
 * @since 2025-09-02 12:23:50
 * @author gewuyou
 */
interface InfoProvider {
    /**
     * 根据名称获取InfoLike对象
     *
     * 通过指定的名称从所有信息对象中查找并返回对应的InfoLike对象。
     * 默认实现通过调用all()方法获取所有信息对象，然后按名称查找。
     *
     * @param name 信息对象的名称
     * @return InfoLike 对应名称的InfoLike对象，如果未找到则返回null
     */
    fun get(name: String): InfoLike? = all()[name]

    /**
     * 获取所有InfoLike对象
     *
     * 返回包含所有可用InfoLike对象的映射表，键为对象名称，值为InfoLike对象。
     * 默认实现返回一个空映射表。
     *
     * @return Map<String, InfoLike> 包含所有信息对象的映射表
     */
    fun all(): Map<String, InfoLike> = emptyMap()
}