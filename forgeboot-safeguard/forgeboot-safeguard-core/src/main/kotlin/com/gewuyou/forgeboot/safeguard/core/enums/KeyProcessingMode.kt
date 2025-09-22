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

package com.gewuyou.forgeboot.safeguard.core.enums

/**
 * 处理模式枚举类
 * 定义了数据处理的不同模式选项
 *
 * @since 2025-09-22 12:55:55
 * @author gewuyou
 */
enum class KeyProcessingMode {
    /**
     * 无处理模式
     * 表示不进行任何特殊处理
     */
    NONE,

    /**
     * IP键处理模式
     * 表示基于IP地址作为前缀键进行处理
     */
    IP_KEY
}
