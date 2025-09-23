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
 * 尝试检查数据类，用于表示一次尝试操作的检查结果
 *
 * @property allowed 是否允许进行尝试操作
 * @property attemptsTtlMs 尝试操作的存活时间（毫秒）
 * @property lockTtlMs 锁定状态的存活时间（毫秒）
 * @property remainingAttempts 剩余尝试次数
 * @property capacity 窗口容量（policy.max）
 * @property retryAfterMs 需要等待重试的时间（毫秒），如果处于锁定状态则返回锁定剩余时间，否则返回尝试操作的存活时间
 * @since 2025-09-22 10:11:10
 * @author gewuyou
 */
data class AttemptCheck(
    val allowed: Boolean,
    val attemptsTtlMs: Long,
    val lockTtlMs: Long,
    val remainingAttempts: Long,   // ← 新增：当前还能失败几次（0 表示没有）
    val capacity: Long,             // ← 新增：窗口容量（policy.max）
) {
    /**
     * 计算需要等待重试的时间
     * 如果处于锁定状态则返回锁定剩余时间，否则返回尝试操作的存活时间
     */
    val retryAfterMs: Long
        get() = if (lockTtlMs > 0) lockTtlMs else attemptsTtlMs
}
