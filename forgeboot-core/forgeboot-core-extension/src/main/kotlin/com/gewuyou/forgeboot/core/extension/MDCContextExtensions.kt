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

package com.gewuyou.forgeboot.core.extension

import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.slf4j.MDC
import kotlin.coroutines.CoroutineContext

/**
 * MDC工具对象，用于操作Mapped Diagnostic Context
 * 提供对当前线程MDC的访问和操作功能
 */
object Mdc {
    /** 当前线程 MDC 的快照（不可变）。*/
    val map: Map<String, String> get() = MDC.getCopyOfContextMap() ?: emptyMap()

    /** 当前线程 MDC 的快照上下文。*/
    val context: MDCContext get() = MDCContext(map)

    // 可选便捷方法
    /**
     * 获取指定键的MDC值
     * @param key 要获取值的键
     * @return 对应键的值，如果不存在则返回null
     */
    operator fun get(key: String): String? = MDC.get(key)

    /**
     * 设置指定键值对到MDC中
     * @param key 要设置的键
     * @param value 要设置的值
     */
    operator fun set(key: String, value: String) = MDC.put(key, value)

    /**
     * 从MDC中移除指定键
     * @param key 要移除的键
     */
    fun remove(key: String) = MDC.remove(key)

    /** 清空MDC中的所有键值对 */
    fun clear() = MDC.clear()
}

/**
 * 扩展函数，将当前MDC上下文添加到协程上下文中
 * @return 包含MDC上下文的新协程上下文
 */
fun CoroutineContext.withMdc(): CoroutineContext = this + Mdc.context

/**
 * 在指定MDC上下文中执行代码块
 * @param block 要执行的代码块
 * @return 代码块的执行结果
 */
suspend inline fun <T> withMdc(crossinline block: suspend () -> T): T =
    withContext(Mdc.context) { block() }
