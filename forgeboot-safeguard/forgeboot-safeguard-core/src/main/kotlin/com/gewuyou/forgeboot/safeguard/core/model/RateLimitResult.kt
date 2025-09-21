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

package com.gewuyou.forgeboot.safeguard.core.model

import java.time.Instant

/**
 * 限流结果数据类
 *
 * @property allowed 是否允许通过限流检查
 * @property remaining 剩余可用次数
 * @property resetAt 限流重置时间
 * @since 2025-09-21 10:54:46
 * @author gewuyou
 */
data class RateLimitResult(
    val allowed: Boolean,
    val remaining: Long,
    val resetAt: Instant?,
)
