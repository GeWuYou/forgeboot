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

/**
 * 尝试限制结果数据类
 *
 * 用于表示一次尝试限制操作的结果，包含是否允许执行、尝试次数的过期时间以及锁定时间等信息
 *
 * @property allowed 是否允许执行操作
 * @property attemptsTtlMs 尝试次数的过期时间（毫秒）
 * @property lockTtlMs 锁定时间（毫秒）
 * @since 2025-09-22 10:49:59
 * @author gewuyou
 */
data class AttemptLimitResult(
    val allowed: Boolean,
    val attemptsTtlMs: Long,
    val lockTtlMs: Long,
)
