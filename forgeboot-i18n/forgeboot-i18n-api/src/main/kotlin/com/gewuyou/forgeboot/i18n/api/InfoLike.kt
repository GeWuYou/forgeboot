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

package com.gewuyou.forgeboot.i18n.api

/**
 * 信息接口，定义了系统中信息对象的基本结构
 *
 * 该接口用于表示系统中的各种信息，包括状态码、消息键和默认消息
 * 实现类可以通过这些属性来提供统一的信息格式
 *
 * @since 2025-09-02 12:06:23
 * @author gewuyou
 */
interface InfoLike {
    /**
     * 状态码
     *
     * 表示操作结果的状态码，通常遵循HTTP状态码规范
     */
    val code: Int

    /**
     * 消息键，用于国际化支持
     *
     * 例如: "ok", "error.validation_failed"
     * 该键用于从资源文件中获取对应语言的消息文本
     */
    val messageKey: String?

    /**
     * 消息参数，用于动态替换消息中的占位符
     *
     * 例如: ["张三", "18"]
     * 在消息文本中，可以使用{}来表示占位符，例如"Hello, {}! You are {} years old."
     * 当调用时传入参数["张三", "18"]，将替换成"Hello, 张三! You are 18 years old."
     */
    val messageArgs: Array<out Any>?

    /**
     * 默认消息，当找不到对应国际化资源时使用的兜底文案
     *
     * 当系统无法根据messageKey找到对应语言的消息时，将使用此默认消息
     */
    val defaultMessage: String
}