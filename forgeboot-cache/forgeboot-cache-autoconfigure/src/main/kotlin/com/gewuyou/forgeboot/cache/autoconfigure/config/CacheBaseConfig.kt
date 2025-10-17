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

import com.gewuyou.forgeboot.cache.api.generator.KeyGenerator
import com.gewuyou.forgeboot.cache.impl.generator.DefaultKeyGenerator
import com.gewuyou.forgeboot.cache.impl.utils.RedisKeyScanner
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory

/**
 * 缓存基础配置类，用于定义缓存相关的基础 Bean。
 *
 * @since 2025-06-20 23:31:09
 * @author gewuyou
 */
@Configuration(proxyBeanMethods = false)
class CacheBaseConfig {
    /**
     * 创建 RedisKeyScanner Bean。
     *
     * RedisKeyScanner 用于扫描 Redis 中的键（Key），便于管理与维护缓存数据。
     *
     * @param redisConnectionFactory 已注入的 Redis 连接工厂实例
     * @return 配置好的 RedisKeyScanner 实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun redisKeyScanner(redisConnectionFactory: RedisConnectionFactory): RedisKeyScanner {
        return RedisKeyScanner(redisConnectionFactory)
    }

    /**
     * 创建默认的 KeyGenerator Bean。
     *
     * DefaultKeyGenerator 用于生成统一格式的缓存键，确保缓存键的命名一致性。
     * 若上下文中不存在 KeyGenerator 类型的 Bean，则自动创建此默认实例。
     *
     * @return 默认的 KeyGenerator 实现实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun keyGenerator(): KeyGenerator {
        return DefaultKeyGenerator()
    }
}