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
 * AttemptPolicy 数据类定义了重试策略的配置参数
 *
 * @property window 时间窗口，在此时间范围内计算尝试次数
 * @property max 最大尝试次数，超过此次数将触发锁定机制
 * @property lock 锁定时长，达到最大尝试次数后锁定的时间
 * @property escalate 升级策略映射，键为尝试次数，值为对应的延迟时间
 * @property successReset 是否在成功后重置尝试计数器
 * @since 2025-09-22 11:03:21
 * @author gewuyou
 */
data class AttemptPolicy(
    val window: Duration,
    val max: Long,
    val lock: Duration,
    val escalate: Map<Long, Duration> = emptyMap(),
    val successReset: Boolean = true,
)
