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

package com.gewuyou.forgeboot.context

import com.gewuyou.forgeboot.context.api.AbstractContext
import com.gewuyou.forgeboot.context.api.Context
import java.util.concurrent.atomic.AtomicReference

/**
 * 上下文持有者
 *
 * 用于全局持有并管理一个线程安全的上下文实例，支持一次性初始化和类型安全的值存取。
 *
 * @since 2025-09-03 12:06:35
 * @author gewuyou
 */
object ContextHolders : AbstractContext<String, String>() {
    /**
     * 上下文实例，使用@Volatile注解确保多线程环境下的可见性
     */
    private val ref = AtomicReference<Context<String, String>?>(null)

    /** 是否已完成初始化 */
    val isInitialized: Boolean get() = ref.get() != null

    /**
     * 一次性初始化。若重复调用并传入不同实例，直接抛错。
     *
     * @param context 上下文实例，用于初始化ContextHolder
     * @throws IllegalStateException 如果已经使用不同的实例初始化过
     */
    fun init(context: Context<String, String>) {
        val old = ref.get()
        if (old == null) {
            // 第一次初始化，使用CAS操作设置实例
            if (!ref.compareAndSet(null, context)) {
                check(ref.get() === context) {
                    "ContextHolders has been initialized with a different instance."
                }
            }
        } else {
            // 非第一次初始化，检查传入的实例是否与已存在的实例相同
            check(old === context) {
                "ContextHolders has already been initialized with a different instance."
            }
        }
    }


    /**
     * 仅供测试使用，用于重置内部持有的上下文实例。
     */
    @JvmStatic
    internal fun resetForTest() {
        ref.set(null)
    }

    /**
     * 获取当前上下文实例，如果尚未初始化则抛出异常。
     *
     * @return 当前持有的上下文实例
     * @throws IllegalStateException 如果尚未调用 init 方法进行初始化
     */
    private fun requireContext(): Context<String, String> =
        ref.get() ?: error("ContextHolders is not initialized yet. Call ContextHolders.init(...) first.")

    /**
     * 将指定映射中的所有键值对添加到当前映射中
     *
     * @param map 包含要添加的键值对的源映射，键为字符串类型，值可以为任意类型或null
     */
    override fun putAll(map: Map<String, Any?>) {
        requireContext().putAll(map)
    }

    /**
     * 根据指定的键和类型从上下文中获取对应的值。
     *
     * @param key  要查找的键
     * @param type 要转换的目标类型
     * @return 对应类型的值，如果不存在或类型不匹配则返回 null
     */
    override fun <T> retrieveByType(key: String, type: Class<T>): T? =
        requireContext().retrieveByType(key, type)

    /**
     * 获取指定键对应的字符串值。
     *
     * @param key 要查找的键
     * @return 对应的字符串值，如果不存在则返回 null
     */
    fun getString(key: String): String? =
        retrieveByType(key, String::class.java)
}
