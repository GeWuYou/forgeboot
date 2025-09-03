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

/**
 *上下文持有人
 *
 * @since 2025-09-03 12:06:35
 * @author gewuyou
 */
object ContextHolders : AbstractContext<String, String>() {
    /**
     * 上下文实例，使用@Volatile注解确保多线程环境下的可见性
     */
    @Volatile
    private var context: Context<String, String>? = null

    /**
     * 初始化上下文持有者
     *
     * @param context 要设置的上下文实例
     */
    fun init(context: Context<String, String>) {
        this.context = context
    }

    /**
     * 将指定映射中的所有键值对添加到当前映射中
     *
     * @param map 包含要添加的键值对的源映射，键为字符串类型，值可以为任意类型或null
     */
    override fun putAll(map: Map<String, Any?>) {
        context?.putAll(map)
    }

    /**
     * 根据指定的键和类型从上下文中获取对应的值。
     *
     * @param key  要查找的键
     * @param type 要转换的目标类型
     * @return 对应类型的值，如果不存在或类型不匹配则返回 null
     */
    override fun <T> retrieveByType(key: String, type: Class<T>): T? {
        return context?.retrieveByType(key, type)
    }
}