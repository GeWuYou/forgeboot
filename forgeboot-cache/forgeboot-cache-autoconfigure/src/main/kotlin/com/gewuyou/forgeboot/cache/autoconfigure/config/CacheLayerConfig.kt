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
import org.springframework.beans.factory.annotation.Qualifier
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
     * 创建缓存层列表的Bean定义
     *
     * @param caches 缓存实例列表，用于创建缓存层
     * @param customizer 缓存层自定义器提供者，可选地对默认缓存层进行自定义
     * @return 缓存层列表，经过自定义器处理后的结果或默认缓存层列表
     */
    @Bean
    @ConditionalOnMissingBean
    fun cacheLayers(
        @Qualifier("layerCache")
        caches: List<Cache>,
        customizer: ObjectProvider<CacheLayerCustomizer>,
    ): List<CacheLayer> {
        // 排除 CompositeCache 实例
        val defaultLayers = caches.mapIndexed { index, cache ->
            CacheLayer(cache, index)
        }
        return customizer.getIfAvailable()?.customize(defaultLayers) ?: defaultLayers
    }

}