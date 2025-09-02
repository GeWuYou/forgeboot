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

package com.gewuyou.forgeboot.i18n.impl.resolver

import com.gewuyou.forgeboot.i18n.api.InfoLike
import com.gewuyou.forgeboot.i18n.api.InfoResolver
import com.gewuyou.forgeboot.i18n.api.entities.ResolvedInfo
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder

/**
 * 基于Spring MessageSource的消息解析器实现
 *
 * 该类使用Spring的MessageSource机制来解析InfoLike对象中的消息键，
 * 获取对应的本地化消息文本。如果找不到对应的消息，则使用默认消息。
 *
 * @property messageSource Spring的消息源，用于获取本地化消息
 * @since 2025-09-02 12:12:49
 * @author gewuyou
 */
class MessageSourceInfoResolver(
    private val messageSource: MessageSource,
) : InfoResolver {
    /**
     * 解析InfoLike对象为ResolvedInfo对象
     *
     * 通过Spring的MessageSource机制，根据当前语言环境(locale)解析消息键，
     * 获取对应的本地化消息文本。如果解析失败，则使用InfoLike对象中的默认消息。
     *
     * @param info 包含状态码和消息键的信息对象
     * @return ResolvedInfo 包含状态码和解析后消息文本的对象
     */
    override fun resolve(info: InfoLike): ResolvedInfo {
        val locale = LocaleContextHolder.getLocale()
        val message = info.messageKey?.let { key ->
            messageSource.getMessage(key, info.messageArgs, locale)
        } ?: info.defaultMessage
        return ResolvedInfo(code = info.code, message = message)
    }

}