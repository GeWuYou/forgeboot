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

package com.gewuyou.forgeboot.i18n.api.entities

/**
 * 已解析的信息
 *
 * @since 2025-09-02 12:10:18
 * @author gewuyou
 */
data class ResolvedInfo(
    /**
     * 状态码
     *
     * 表示操作结果的状态码，通常遵循HTTP状态码规范
     */
    val code: Int,

    /**
     * 解析后的消息文本
     *
     * 根据消息键解析得到的本地化消息文本
     */
    val message: String,
)