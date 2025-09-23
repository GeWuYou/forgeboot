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

package com.gewuyou.forgeboot.safeguard.core.factory

/**
 * 异常工厂接口，用于根据给定的输入创建对应的运行时异常
 *
 * @param T 输入参数的类型
 * @since 2025-09-23 20:53:01
 * @author gewuyou
 */
fun interface ExceptionFactory<T> {
    /**
     * 根据输入参数创建运行时异常
     *
     * @param ctx 用于创建异常的输入参数
     * @return 创建的运行时异常实例
     */
    fun create(ctx: T): RuntimeException
}