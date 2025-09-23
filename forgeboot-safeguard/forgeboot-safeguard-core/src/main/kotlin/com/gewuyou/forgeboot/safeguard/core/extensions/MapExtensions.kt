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

package com.gewuyou.forgeboot.safeguard.core.extensions

import java.time.Duration

/**
 * 将Map<Long, Duration>转换为CSV格式的字符串
 *
 * @return 返回格式为"k1:v1,k2:v2,..."的字符串，其中k是键，v是持续时间的毫秒值
 *         结果按键的升序排列
 */
fun Map<Long, Duration>.toCsv(): String =
    entries.sortedBy { it.key } // 按键排序
        .joinToString(",") { (k, v) -> "$k:${v.toMillis()}" } // 转换为key:millis格式并用逗号连接