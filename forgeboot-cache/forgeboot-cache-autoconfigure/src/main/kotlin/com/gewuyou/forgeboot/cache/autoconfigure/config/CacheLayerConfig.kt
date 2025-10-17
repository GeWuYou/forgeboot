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

package com.gewuyou.forgeboot.cache.autoconfigure.config

import com.gewuyou.forgeboot.cache.api.contract.Cache
import com.gewuyou.forgeboot.cache.api.customizer.CacheLayerCustomizer
import com.gewuyou.forgeboot.cache.api.entities.CacheLayer
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 缓存层配置
 *
 * 该配置类用于定义多级缓存的层级结构，当前实现中包含两级缓存：
 * 第一层为基于 Caffeine 的本地缓存，具有每个条目独立的生存时间（TTL）特性；
 * 第二层为基于 Redis 的分布式缓存，用于跨服务共享缓存数据。
 *
 * @since 2025-06-21 11:39:35
 * @author gewuyou
 */
@Configuration
class CacheLayerConfig {

    /**
     * 定义缓存层级结构，Caffeine 缓存在第一层，Redis 缓存在第二层
     *
     * 该方法创建并返回一个包含两个缓存层的列表。每层缓存都与一个具体的缓存实现绑定，
     * 并指定其在整体架构中的优先级顺序。第一层使用本地的 Caffeine 缓存以提高访问速度，
     * 第二层使用 Redis 缓存来保证数据的共享和持久性。
     *
     * @return 包含两个缓存层的列表，按优先级排序
     */
    @Bean
    @ConditionalOnMissingBean
    fun cacheLayers(
        caches: List<Cache>,
        customizer: ObjectProvider<CacheLayerCustomizer>,
    ): List<CacheLayer> {
        val defaultLayers = caches.mapIndexed { index, cache ->
            CacheLayer(cache, index)
        }
        return customizer.getIfAvailable()?.customize(defaultLayers) ?: defaultLayers
    }
}