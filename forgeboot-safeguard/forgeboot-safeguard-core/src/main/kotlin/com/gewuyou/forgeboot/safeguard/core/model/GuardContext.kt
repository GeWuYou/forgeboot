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

import com.gewuyou.forgeboot.safeguard.core.key.Key
import org.aspectj.lang.ProceedingJoinPoint
import java.lang.reflect.Method
import java.time.Instant

/**
 * 后卫上下文
 *
 * 用于存储和传递后卫处理过程中的上下文信息，包含方法执行相关的元数据和环境信息
 *
 * @property key 标识当前上下文的唯一键值
 * @property scene 场景标识，可为空
 * @property infoCode 信息编码，可为空
 * @property joinPoint 切点对象，用于AOP拦截的方法执行点
 * @property method 当前执行的方法对象
 * @property now 当前时间戳
 * @property args 方法参数数组
 * @property currentAnnotation 当前处理的注解对象
 * @property annotations 方法上的注解数组
 * @since 2025-09-23 11:07:01
 * @author gewuyou
 */
open class GuardContext(
    open val key: Key,
    open val scene: String?,
    open val infoCode: String?,
    open val joinPoint: ProceedingJoinPoint,
    open val method: Method,
    open val now: Instant,
    open val args: Array<Any?>,
    open val currentAnnotation: Annotation,
    open val annotations: Array<Annotation>,
)

