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

package com.gewuyou.forgeboot.safeguard.core.util

/**
 * 类型转换工具类
 *
 * @since 2025-09-22 10:31:33
 * @author gewuyou
 */
object ConversionUtils {
    /**
     * 将任意类型转换为 Long 类型。
     *
     * @param x 待转换的值
     * @return 转换后的 Long 值
     */
    fun asLong(x: Any?): Long = when (x) {
        null -> 0L
        is Number -> x.toLong()
        is String -> x.toLong()
        is ByteArray -> String(x, Charsets.UTF_8).toLong()
        else -> error("Unexpected type: ${x::class}")
    }


}