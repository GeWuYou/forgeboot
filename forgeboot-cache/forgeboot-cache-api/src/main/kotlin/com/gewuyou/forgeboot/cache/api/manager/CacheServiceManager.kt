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

package com.gewuyou.forgeboot.cache.api.manager

import com.gewuyou.forgeboot.cache.api.service.CacheService

/**
 * 缓存管理器
 *
 * 提供对不同缓存服务的统一访问接口，通过指定的命名空间获取对应的缓存服务实例。
 * 该接口定义了核心方法用于获取泛型化的缓存服务对象。
 *
 * @since 2025-06-16 22:11:06
 * @author gewuyou
 */
interface CacheServiceManager {

    /**
     * 获取指定命名空间的缓存服务实例
     *
     * @param namespace 缓存服务的命名空间标识
     * @return 返回与命名空间关联的 CacheService 实例
     */
    fun getCache(namespace: String): CacheService

    /**
     * 清除指定命名空间下的所有缓存数据
     *
     * @param namespace 要清除缓存数据的命名空间标识
     */
    fun clear(namespace: String)

    /**
     * 清除所有命名空间下的缓存数据
     *
     * 通常用于全局缓存刷新或系统清理操作
     */
    fun clearAll()
}