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

package com.gewuyou.forgeboot.safeguard.core.key

/**
 * 键
 *
 * @since 2025-09-21 09:52:22
 * @author gewuyou
 * @param namespace 命名空间，用于区分不同的业务领域，例如 "email.send", "order.create"
 * @param value 业务键，具体业务场景下的唯一标识，例如 "user@example.com:REGISTER"
 */
data class Key(
    val namespace: String,         // e.g. "email.send", "order.create"
    val value: String,              // 业务键，如 "user@example.com:REGISTER"
) {
    /**
     * 获取完整的键字符串表示
     *
     * @return 返回格式为 "namespace::value" 的完整键字符串
     */
    fun full(): String = "$namespace::$value"
}
