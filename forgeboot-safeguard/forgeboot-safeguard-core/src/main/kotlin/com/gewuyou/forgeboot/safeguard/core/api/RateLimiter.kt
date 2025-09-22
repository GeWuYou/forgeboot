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

package com.gewuyou.forgeboot.safeguard.core.api

import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.model.RateLimitResult
import com.gewuyou.forgeboot.safeguard.core.policy.RateLimitPolicy

/**
 * 限流器接口，用于控制请求的速率限制
 *
 * @since 2025-09-21 11:39:12
 * @author gewuyou
 */
interface RateLimiter {
    /**
     * 尝试消费一个限流令牌
     *
     * @param key 限流键，用于标识不同的限流对象
     * @param policy 限流策略，定义限流规则和参数
     * @return 限流结果，包含是否允许执行以及相关限流信息
     */
    fun tryConsume(key: Key, policy: RateLimitPolicy): RateLimitResult

    /**
     * 退还指定数量的限流令牌
     *
     * @param key 限流键，用于标识不同的限流对象
     * @param requested 请求退还的令牌数量
     * @param policy 限流策略，定义限流规则和参数
     * @return 实际退还的令牌数量
     */
    fun refund(key: Key, requested: Long, policy: RateLimitPolicy): Long
}
