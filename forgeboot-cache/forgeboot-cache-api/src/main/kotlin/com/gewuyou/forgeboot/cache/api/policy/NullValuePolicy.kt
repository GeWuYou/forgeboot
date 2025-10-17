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

package com.gewuyou.forgeboot.cache.api.policy

/**
 * Null 值策略接口
 *
 * 用于定义缓存中 null 值的处理策略，包括是否允许缓存 null 值、获取 null 占位符以及判断值是否为 null 占位符。
 *
 * @since 2025-06-16 22:15:13
 * @author gewuyou
 */
interface NullValuePolicy {
    /**
     * 判断给定的 key 是否允许缓存 null 值。
     *
     * @param key 缓存项的键
     * @return Boolean 返回 true 表示允许缓存 null 值，否则不允许
     */
    fun allowCacheNull(key: String): Boolean

    /**
     * 获取用于表示 null 值的占位符字符串。
     *
     * @return String? 返回 null 值的占位符
     */
    fun nullPlaceholder(): String?

    /**
     * 判断给定的 value 是否为 null 占位符。
     *
     * @param value 要判断的值
     * @return Boolean 返回 true 表示是 null 占位符，否则不是
     */
    fun isNullPlaceholder(value: String): Boolean
}