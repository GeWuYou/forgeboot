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

import com.gewuyou.forgeboot.i18n.api.entities.ResolvedInfo

/**
 * 信息解析器接口
 *
 * 用于将InfoLike对象解析为ResolvedInfo对象，主要负责根据消息键获取本地化消息文本
 * 这是一个SPI接口，允许外部提供自定义实现来替换默认解析逻辑
 *
 * @since 2025-09-02 12:09:34
 * @author gewuyou
 */
fun interface InfoResolver {
    /**
     * 解析InfoLike对象为ResolvedInfo对象
     *
     * 根据InfoLike对象中的messageKey获取对应的本地化消息文本，
     * 如果提供了参数args，则会将其用于消息文本中的占位符替换
     *
     * @param info 包含状态码和消息键的信息对象
     * @return ResolvedInfo 包含状态码和解析后消息文本的对象
     */
    fun resolve(info: InfoLike): ResolvedInfo
}