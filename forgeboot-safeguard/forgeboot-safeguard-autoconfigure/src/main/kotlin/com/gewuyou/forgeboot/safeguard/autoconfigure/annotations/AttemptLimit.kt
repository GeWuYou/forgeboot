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

package com.gewuyou.forgeboot.safeguard.autoconfigure.annotations

import com.gewuyou.forgeboot.safeguard.core.enums.KeyProcessingMode

/**
 * 尝试限制注解，用于限制函数执行的尝试次数
 *
 * @param window 时间窗口，ISO-8601格式，如PT5M表示5分钟，默认PT10M（10分钟）
 * @param max 时间窗口内最大失败次数，默认6次
 * @param lock 触发限制后的硬锁定时长，ISO-8601格式，默认PT30M（30分钟）
 * @param escalate 阶梯式限制策略，格式为"次数:时长,次数:时长"，默认为空
 * @param mode 键处理模式，默认为IP_KEY即使用IP作为键前缀
 * @param key SpEL表达式，用于定义补充的限制维度，如"'phone:'+ #req.phone"，默认为空
 * @param successReset 执行成功后是否重置计数器，默认为true
 *
 * @since 2025-09-22 09:40:46
 * @author gewuyou
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AttemptLimit(
    val window: String = "PT10M",             // ISO-8601 ，如 PT5M
    val max: Int = 6,                         // 窗口内最大失败次数
    val lock: String = "PT30M",               // 触发后的硬锁时长
    val escalate: String = "",                // 阶梯："10:PT12H,15:P7D"
    val mode: KeyProcessingMode = KeyProcessingMode.IP_KEY,
    val key: String = "",                     // SpEL，补充维度，如 "'phone:'+ #req.phone"
    val template: String = "",
    val resolverBean: String = "",
    val successReset: Boolean = true,         // 成功是否清零
)
