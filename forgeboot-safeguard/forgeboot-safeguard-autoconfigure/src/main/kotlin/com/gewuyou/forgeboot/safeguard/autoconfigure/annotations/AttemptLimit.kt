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
import com.gewuyou.forgeboot.safeguard.core.factory.AttemptLimitExceptionFactory
import kotlin.reflect.KClass

/**
 * 尝试限制注解，用于限制函数执行的尝试次数。当函数调用失败达到指定次数时，
 * 可以触发冷却机制，在一定时间内阻止进一步的调用。
 *
 * @param window 时间窗口，采用 ISO-8601 格式表示，如 PT5M 表示 5 分钟，默认值为 PT10M（10分钟）。
 * @param max 在指定时间窗口内允许的最大失败次数，默认为 6 次。
 * @param lock 触发限制后进入硬锁定状态的持续时间，同样使用 ISO-8601 格式，默认为 PT30M（30分钟）。
 * @param escalate 阶梯式限制策略配置，格式为 "次数:时长,次数:时长"，例如 "10:PT12H,15:P7D"，默认为空。
 * @param mode 键处理模式，决定限流键的生成方式，默认为 IP_KEY，即使用客户端 IP 作为键前缀。
 * @param key SpEL 表达式，用于定义额外的限流维度，例如 "'phone:'+ #req.phone"，默认为空字符串。
 * @param template key 的模板字符串，用于生成限流键，默认为空。
 * @param resolverBean 用于解析 key 的 Spring Bean 名称，支持动态键生成，默认为空。
 * @param successReset 函数执行成功后是否重置失败计数器，默认为 true。
 * @param scene 场景标识符，可用于区分不同的业务场景，默认为空。
 * @param infoCode 错误码，用于标识触发限制时返回的错误类型，默认为空。
 * @param factory 异常工厂类，用于创建尝试限制异常实例。
 * @param factoryBean 异常工厂对应的 Spring Bean 名称，用于获取异常工厂实例，默认为空。
 *
 * @since 2025-09-22 09:40:46
 * @author gewuyou
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AttemptLimit(
    val window: String = "PT10M",             // ISO-8601 ，如 PT5M
    val max: Long = 6,                         // 窗口内最大失败次数
    val lock: String = "PT30M",               // 触发后的硬锁时长
    val escalate: String = "",                // 阶梯："10:PT12H,15:P7D"
    val mode: KeyProcessingMode = KeyProcessingMode.IP_KEY,
    val key: String = "",                     // SpEL，补充维度，如 "'phone:'+ #req.phone"
    val template: String = "",
    val resolverBean: String = "",
    val successReset: Boolean = true,         // 成功是否清零
    val scene: String = "",
    val infoCode: String = "",
    val factory: KClass<out AttemptLimitExceptionFactory> =
        AttemptLimitExceptionFactory::class,
    val factoryBean: String = "",
)
