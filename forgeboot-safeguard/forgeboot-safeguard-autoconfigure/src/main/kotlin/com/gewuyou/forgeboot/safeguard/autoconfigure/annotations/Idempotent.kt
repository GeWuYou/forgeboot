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

import com.gewuyou.forgeboot.safeguard.core.enums.IdemMode

/**
 * 幂等性注解，用于标记需要保证幂等性的函数
 * 通过指定唯一的key和过期时间来实现接口的幂等性控制
 *
 * @property key SpEL表达式，用于指定唯一标识符的来源，可以是header、参数或DTO中的requestId
 * @property ttlSeconds SpEL表达式，指定幂等记录的过期时间，单位为秒，例如"600"
 * @property mode 幂等处理模式，默认为RETURN_SAVED，表示返回已保存的结果
 * @since 2025-09-21 14:17:53
 * @author gewuyou
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Idempotent(
    val key: String,
    val ttlSeconds: String,
    val template: String = "",
    val resolverBean: String = "",
    val mode: IdemMode = IdemMode.RETURN_SAVED,
)
