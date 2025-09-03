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

package com.gewuyou.forgeboot.trace.impl.provider

import com.gewuyou.forgeboot.context.ContextHolders
import com.gewuyou.forgeboot.context.api.extension.get
import com.gewuyou.forgeboot.trace.api.RequestIdProvider
import com.gewuyou.forgeboot.trace.api.config.TraceProperties
import org.slf4j.MDC


/**
 *跟踪请求ID提供商
 *
 * @since 2025-05-03 17:26:46
 * @author gewuyou
 */
class TraceRequestIdProvider(
    private val traceProperties: TraceProperties,
) : RequestIdProvider {
    /**
     * 获取请求ID
     *
     * 返回一个唯一的字符串标识符作为请求ID
     *
     * @return 请求ID的字符串表示
     */
    override fun getRequestId(): String {
        return ContextHolders[traceProperties.requestIdMdcKey] ?: MDC.get(traceProperties.requestIdMdcKey)
        ?: throw RuntimeException("requestId is null")
    }
}