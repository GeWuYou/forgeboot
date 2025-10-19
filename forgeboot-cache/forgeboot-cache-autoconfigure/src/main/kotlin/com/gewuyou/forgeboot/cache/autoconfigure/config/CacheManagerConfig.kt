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

import com.gewuyou.forgeboot.cache.api.config.CacheProperties
import com.gewuyou.forgeboot.cache.api.contract.Cache
import com.gewuyou.forgeboot.cache.api.generator.KeyGenerator
import com.gewuyou.forgeboot.cache.api.manager.CacheServiceManager
import com.gewuyou.forgeboot.cache.impl.manager.DefaultCacheServiceManager
import com.gewuyou.forgeboot.core.serialization.serializer.ValueSerializer
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 缓存管理器配置
 *
 * 配置类用于定义缓存管理相关的 Bean，主要职责是创建和初始化 CacheManager 实例。
 * 该配置类基于 Spring 的 @Configuration 注解标识为配置类，并通过 @Bean 定义核心 Bean。
 *
 * @since 2025-06-21 11:41:05
 * @author gewuyou
 */
@Configuration
class CacheManagerConfig {

    /**
     * 创建并配置 CacheManager 缓存管理器 Bean。
     *
     * 此方法负责实例化一个 DefaultCacheManager，它依赖以下组件：
     * - cacheProperties：提供缓存的全局配置参数。
     * - cache：实现缓存数据存储与访问的核心接口。
     * - serializer：用于序列化和反序列化缓存值。
     * - keyGenerator：生成缓存键的策略接口。
     *
     * 返回的 CacheManager 是一个具体实现，用于管理整个应用中的缓存操作。
     *
     * @param cacheProperties 缓存配置信息
     * @param cache 缓存存储与访问的具体实现
     * @param serializer 序列化/反序列化工具
     * @param keyGenerator 缓存键生成策略
     * @return 初始化完成的缓存管理器实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun cacheServiceManager(
        cacheProperties: CacheProperties,
        cache: Cache,
        serializer: ValueSerializer,
        keyGenerator: KeyGenerator,
    ): CacheServiceManager {
        return DefaultCacheServiceManager(
            cacheProperties,
            cache,
            serializer,
            keyGenerator
        )
    }
}
