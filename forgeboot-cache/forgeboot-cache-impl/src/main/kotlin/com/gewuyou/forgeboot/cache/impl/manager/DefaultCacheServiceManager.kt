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

package com.gewuyou.forgeboot.cache.impl.manager

import com.gewuyou.forgeboot.cache.api.config.CacheProperties
import com.gewuyou.forgeboot.cache.api.contract.Cache
import com.gewuyou.forgeboot.cache.api.generator.KeyGenerator
import com.gewuyou.forgeboot.cache.api.manager.CacheServiceManager
import com.gewuyou.forgeboot.cache.api.service.CacheService
import com.gewuyou.forgeboot.cache.impl.service.DefaultCacheService
import com.gewuyou.forgeboot.core.serialization.serializer.ValueSerializer
import java.util.concurrent.ConcurrentHashMap

/**
 * 默认缓存管理器
 *
 * 该类负责管理不同命名空间下的缓存服务实例，通过懒加载方式创建 CacheService，
 * 并提供统一的清除缓存接口。
 *
 * @since 2025-06-18 13:39:07
 * @author gewuyou
 */
class DefaultCacheServiceManager(
    private val cacheProperties: CacheProperties,
    private val cache: Cache,
    private val serializer: ValueSerializer,
    private val keyGenerator: KeyGenerator,
) : CacheServiceManager {

    /**
     * 缓存服务实例的线程安全映射表，键为命名空间，值为对应的 CacheService 实例。
     * 使用 ConcurrentHashMap 确保多线程环境下的安全访问。
     */
    private val serviceMap = ConcurrentHashMap<String, CacheService>()

    /**
     * 获取指定命名空间的缓存服务。
     *
     * 如果该命名空间尚未存在，则使用提供的参数创建一个新的 DefaultCacheService 实例；
     * 否则返回已存在的实例。
     *
     * @param namespace 缓存命名空间标识符
     * @return 对应命名空间的缓存服务实例
     */
    override fun getCache(namespace: String): CacheService {
        return serviceMap.computeIfAbsent(namespace) {
            DefaultCacheService(
                serializer = serializer,
                cache = cache,
                namespace = namespace,
                cacheProperties = cacheProperties,
                keyGenerator = keyGenerator
            )
        }
    }

    /**
     * 清除指定命名空间的缓存内容。
     *
     * 如果该命名空间存在对应的缓存服务，则调用其 clear 方法进行清除。
     *
     * @param namespace 要清除缓存的命名空间标识符
     */
    override fun clear(namespace: String) {
        serviceMap[namespace]?.clear(namespace)
    }

    /**
     * 清除所有命名空间的缓存内容。
     *
     * 遍历 serviceMap 中的所有缓存服务，并分别调用它们的 clear 方法。
     */
    override fun clearAll() {
        serviceMap.forEach { (ns, svc) -> svc.clear(ns) }
    }
}