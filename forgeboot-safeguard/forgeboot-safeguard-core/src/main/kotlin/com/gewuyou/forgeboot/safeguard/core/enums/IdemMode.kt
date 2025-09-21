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
 * 幂等性模式枚举类
 * 定义了处理幂等性请求的三种模式
 *
 * @since 2025-09-21 09:57:24
 * @author gewuyou
 */
enum class IdemMode {
    /**
     * 返回已保存的结果
     * 当检测到重复请求时，直接返回之前保存的处理结果
     */
    RETURN_SAVED,

    /**
     * 返回409冲突状态
     * 当检测到重复请求时，返回HTTP 409 Conflict状态码
     */
    CONFLICT_409,

    /**
     * 等待直到处理完成
     * 当检测到重复请求时，等待前一个相同请求处理完成后返回结果
     */
    WAIT_UNTIL_DONE
}