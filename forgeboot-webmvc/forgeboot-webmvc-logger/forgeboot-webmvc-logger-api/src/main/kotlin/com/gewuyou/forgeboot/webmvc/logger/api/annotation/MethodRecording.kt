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

package com.gewuyou.forgeboot.webmvc.logger.api.annotation

/**
 * 记录方法的注解： 记录出参入参，方法执行耗时等信息
 *
 * @since 2025-01-23 14:22:11
 * @author gewuyou
 */
// 注解用于函数
@Target(AnnotationTarget.FUNCTION)
// 在运行时可用
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
/**
 * 方法记录注解类
 *
 * @param description 方法描述信息
 * @param printArgs 是否打印方法参数
 * @param printResult 是否打印方法返回值
 * @param printTime 是否打印方法执行耗时
 */
annotation class MethodRecording(
    val description: String = "",
    val printArgs: Boolean = true,
    val printResult: Boolean = true,
    val printTime: Boolean = true,
)
