/*
 *
 *  *
 *  *  * Copyright (c) 2025 GeWuYou
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *  *
 *  *
 *  *
 *
 */

package com.gewuyou.forgeboot.context.impl.extension

import com.gewuyou.forgeboot.context.api.Context

/**
 * 通过操作符重载实现 Context 的 get 方法，用于根据指定的键获取对应的值。
 *
 * @param key 键，用于查找上下文中的值
 * @return 返回与键对应的值，如果不存在则返回 null
 */
operator fun <K, V> Context<K, V>.get(key: K): V? {
    return this.retrieve(key)
}

/**
 * 通过操作符重载实现 Context 的 set 方法，用于将键值对存储到上下文中。
 *
 * @param key 键，用于标识要存储的值
 * @param value 要存储的值，可以为 null
 */
operator fun <K, V> Context<K, V>.set(key: K, value: V?) {
    this.put(key, value)
}

/**
 * 通过操作符重载实现 Context 的 get 方法，用于根据指定的键和类型获取对应的值。
 *
 * @param key 键，用于查找上下文中的值
 * @param type 指定的值类型，用于类型安全地获取值
 * @return 返回与键和类型对应的值，如果不存在则返回 null
 */
operator fun <K, V, T> Context<K, V>.get(key: K, type: Class<T>): T? {
    return this.retrieveByType(key, type)
}
