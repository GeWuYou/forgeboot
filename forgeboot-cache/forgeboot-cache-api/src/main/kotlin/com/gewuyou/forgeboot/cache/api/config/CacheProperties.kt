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

package com.gewuyou.forgeboot.cache.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * 缓存属性配置类
 *
 * 该类用于定义缓存相关的配置属性，通过@ConfigurationProperties绑定配置前缀"forgeboot.cache"
 * 支持配置默认缓存过期时间（TTL）
 *
 * @property theDefaultCacheTTL 默认缓存过期时间（Time To Live），用于指定缓存项在未被访问后的最大存活时间，默认值为15分钟
 * @property nullValuePlaceholder 缓存项为null时使用的占位符，默认值为"__NULL__"
 * @property redis Redis缓存配置
 * @property caffeine Caffeine缓存配置
 * @since 2025-06-17 10:04:48
 * @author gewuyou
 */
@ConfigurationProperties("forgeboot.cache")
data class CacheProperties(
    /**
     * 默认缓存过期时间（Time To Live）
     *
     * 用于指定缓存项在未被访问后的最大存活时间
     * 默认值为15分钟
     */
    var theDefaultCacheTTL: Duration = Duration.ofMinutes(15),
    /**
     * 缓存项为null时使用的占位符
     *
     * 用于在缓存项为null时返回的占位符
     * 默认值为"__NULL__"
     */
    var nullValuePlaceholder: String = "__NULL__",
    var redis: CacheConfig = CacheConfig(),
    var caffeine: CacheConfig = CacheConfig(),
) {
    /**
     * 缓存配置内部类
     *
     * 用于定义具体缓存实现的配置选项
     *
     * @property enabled 是否启用该缓存实现，默认为true
     */
    data class CacheConfig(
        val enabled: Boolean = true,
    )
}
