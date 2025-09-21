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
 * 冷却策略数据类，用于定义缓存或操作的过期时间策略
 *
 * @property ttl 时间间隔，表示冷却时间的持续时间
 * @since 2025-09-21 09:55:45
 * @author gewuyou
 */
data class CooldownPolicy(
    val ttl: Duration,
)
