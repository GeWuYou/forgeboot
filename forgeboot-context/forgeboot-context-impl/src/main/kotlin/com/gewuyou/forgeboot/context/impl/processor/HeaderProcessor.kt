/*
 *
 *  *
 *  *  * Copyright (c) 2025 GeWuYou
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *  *
 *  *
 *  *
 *
 */

package com.gewuyou.forgeboot.context.impl.processor

import com.gewuyou.forgeboot.context.api.ContextProcessor
import com.gewuyou.forgeboot.context.api.FieldRegistry
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.server.ServerWebExchange

/**
 * 请求头处理器
 *
 * @since 2025-06-04 15:14:57
 * @author gewuyou
 */
class HeaderProcessor(private val reg: FieldRegistry) : ContextProcessor {
    /**
     * 获取当前处理器的执行顺序优先级。
     *
     * 默认实现返回0，数值越小优先级越高。
     *
     * @return Int 表示当前处理器的顺序值
     */
    override fun order(): Int {
        return 10
    }

    /**
     * 从给定的载体中提取上下文信息，并填充到上下文对象中。
     *
     * 默认实现为空方法，子类可根据需要重写此方法。
     *
     * @param carrier 载体对象，通常包含上下文数据
     * @param ctx 可变映射，用于存储提取出的上下文键值对
     */
    override fun extract(carrier: Any, ctx: MutableMap<String, String>) {
        when (carrier) {
            is ServerWebExchange -> reg.all().forEach { def ->
                // 从ServerWebExchange请求头中提取指定字段并存入上下文
                carrier.request.headers[def.header]?.firstOrNull()?.let { value ->
                    if (value.isNotBlank()) {
                        ctx[def.key] = value
                    }
                }
            }
            is HttpServletRequest -> reg.all().forEach { def ->
                // 从HttpServletRequest请求头中提取指定字段并存入上下文
                carrier.getHeader(def.header)?.let { value ->
                    if (value.isNotBlank()) {
                        ctx[def.key] = value
                    }
                }
            }
        }
    }

    /**
     * 将上下文信息注入到给定的载体中。
     *
     * 默认实现为空方法，子类可根据需要重写此方法。
     *
     * @param carrier 载体对象，将上下文数据注入其中
     * @param ctx 包含上下文键值对的映射
     */
    override fun inject(carrier: Any, ctx: MutableMap<String, String>) {
        when (carrier) {
            is ServerWebExchange.Builder -> reg.all().forEach { def ->
                // 向ServerWebExchange构建器中注入请求头字段
                ctx[def.key]?.let { value ->
                    if (value.isNotBlank()) {
                        carrier.request { reqBuilder ->
                            reqBuilder.header(def.header, value)
                        }
                    }
                }
            }
            is HttpServletResponse -> reg.all().forEach { def ->
                // 向HttpServletResponse中设置对应的响应头字段
                ctx[def.key]?.let { value ->
                    if (value.isNotBlank()) {
                        carrier.setHeader(def.header, value)
                    }
                }
            }
        }
    }
}