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

package com.gewuyou.forgeboot.context.api

/**
 * Any值支持
 *
 * @since 2025-09-14 21:18:08
 * @author gewuyou
 */
interface AnyValueSupport {
    /**
     * 存储键值对，值为任意类型
     *
     * @param key 键名
     * @param value 任意类型的值，可为null
     */
    fun putAny(key: String, value: Any?)

    /**
     * 批量存储键值对，值为任意类型
     *
     * @param map 包含任意类型值的键值对映射
     */
    fun putAllAny(map: Map<String, Any?>) {
        map.forEach { (k, v) -> putAny(k, v) }
    }
}
