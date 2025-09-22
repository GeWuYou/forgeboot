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

package com.gewuyou.forgeboot.safeguard.redis.key

import com.gewuyou.forgeboot.safeguard.core.key.Key

/**
 * Redis键构建器类，用于生成不同用途的Redis键
 *
 * @property prefix 键前缀，默认为"sg"
 * @since 2025-09-21 12:39:20
 * @author gewuyou
 */
class RedisKeyBuilder(private val prefix: String = "sg") {
    /**
     * 构建冷却键
     * 生成格式为: {prefix}:cd:{namespace}:{value}
     *
     * @param key 键对象，包含命名空间和值
     * @return 构建完成的冷却键字符串
     */
    fun cooldown(key: Key) = "$prefix:cd:${key.namespace}:${key.value}"

    /**
     * 构建幂等键
     * 生成格式为: {prefix}:id:{namespace}:{value}
     *
     * @param key 键对象，包含命名空间和值
     * @return 构建完成的幂等键字符串
     */
    fun idem(key: Key) = "$prefix:id:${key.namespace}:${key.value}"

    /**
     * 构建限流键
     * 生成格式为: {prefix}:rl:{namespace}:{value}
     *
     * @param key 键对象，包含命名空间和值
     * @return 构建完成的限流键字符串
     */
    fun rateLimit(key: Key) = "$prefix:rl:${key.namespace}:${key.value}"

    /**
     * 生成尝试限制的键名
     *
     * @param key 用于生成限制键名的Key对象
     * @return 返回格式化的字符串，格式为"$prefix:al:${key.namespace}:${key.value}"
     */
    fun attemptLimit(key: Key) = "$prefix:al:${key.namespace}:${key.value}"
}
