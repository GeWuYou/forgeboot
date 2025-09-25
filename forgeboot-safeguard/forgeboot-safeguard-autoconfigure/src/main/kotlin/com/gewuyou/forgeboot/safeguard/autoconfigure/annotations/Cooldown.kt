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

import com.gewuyou.forgeboot.safeguard.core.factory.CooldownExceptionFactory
import kotlin.reflect.KClass

/**
 * 冷却注解，用于标记需要冷却控制的函数
 * 该注解通过SpEL表达式来动态计算冷却key和冷却时间
 *
 * @property key SpEL表达式，用于生成冷却的唯一标识符
 * @property seconds SpEL表达式，用于指定冷却时间（秒）
 * @property scene 场景标识，用于区分不同的业务场景
 * @property infoCode 信息码，用于标识冷却相关的业务信息
 * @property template 模板字符串，用于生成冷却相关的提示信息
 * @property resolverBean 解析器Bean名称，用于指定处理冷却逻辑的Bean
 * @property rollbackOn 指定在发生哪些异常时需要回滚冷却状态
 * @property factory 冷却异常工厂类，用于创建冷却期间的自定义异常
 * @property factoryBean 冷却异常工厂Bean名称，用于指定创建冷却异常的工厂Bean
 * @since 2025-09-21 14:17:21
 * @author gewuyou
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Cooldown(
    val key: String,
    val seconds: String,
    val scene: String = "",
    val infoCode: String = "",
    val template: String = "",
    val resolverBean: String = "",
    val rollbackOn: Array<KClass<out Throwable>> = [],
    val factory: KClass<out CooldownExceptionFactory> =
        CooldownExceptionFactory::class,
    val factoryBean: String = "",
)

