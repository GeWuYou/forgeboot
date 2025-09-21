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

package com.gewuyou.forgeboot.safeguard.core.policy

import java.time.Duration

/**
 * 限流策略数据类，用于定义令牌桶算法的配置参数
 *
 * @property capacity 桶容量，表示令牌桶最多能容纳的令牌数量
 * @property refillTokens 每周期补充的令牌数量
 * @property refillPeriod 补充周期，定义令牌补充的时间间隔
 * @property timeout 等待许可的最长时间，默认为0表示不等待
 * @property requested 需要的令牌数量，默认为1
 * @since 2025-09-21 09:54:38
 * @author gewuyou
 */
data class RateLimitPolicy(
    val capacity: Long,
    val refillTokens: Long,
    val refillPeriod: Duration,
    val timeout: Duration = Duration.ZERO,
    val requested: Long = 1,
)
