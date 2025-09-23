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

package com.gewuyou.forgeboot.webmvc.exception.api.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 异常建议属性配置类
 *
 * 用于配置Web MVC异常处理的相关属性，包括是否启用异常处理和降级消息内容
 *
 * @property enabled 是否启用异常处理功能，默认为true
 * @property fallbackMessage 异常降级时返回给用户的消息，默认为"服务器开小差了，请稍后再试!"
 * @since 2025-09-23 21:07:42
 * @author gewuyou
 */
@ConfigurationProperties("forgeboot.webmvc.exception")
class ExceptionAdviceProperties {
    var enabled: Boolean = true
    var fallbackMessage: String = "服务器开小差了，请稍后再试!"
}
