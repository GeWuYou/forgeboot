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

package com.gewuyou.forgeboot.trace.demo.controller

import com.gewuyou.forgeboot.context.api.ContextProcessor
import com.gewuyou.forgeboot.context.impl.ContextHolder
import com.gewuyou.forgeboot.context.impl.coroutine.ContextAwareCoroutineScope
import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.trace.api.RequestIdProvider
import com.gewuyou.forgeboot.webmvc.dto.api.entities.SuccessMessage
import com.gewuyou.forgeboot.webmvc.dto.impl.Responses
import com.gewuyou.forgeboot.webmvc.logger.api.annotation.MethodRecording
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 *MVC控制器
 *
 * @since 2025-07-17 17:13:44
 * @author gewuyou
 */
@RestController
@RequestMapping("/trace")
class TraceTestController(
    private val requestIdProvider: RequestIdProvider,
    private val contextHolder: ContextHolder,
    private val processors: List<ContextProcessor>,
) {
    @GetMapping("/coroutine")
    @MethodRecording(description = "测试方法记录")
    suspend fun coroutine(): SuccessMessage {
        val requestId = requestIdProvider.getRequestId()
        log.info("→ Controller RequestId: $requestId")

        // 模拟内部异步任务（3秒后完成）
        val scope = ContextAwareCoroutineScope(contextHolder,processors)
        scope.launchWithContext {
            log.info("RID: ${requestIdProvider.getRequestId()}")
        }
        return Responses.ok("Main coroutine returned immediately with requestId: $requestId")
    }

    @GetMapping("/servlet")
    fun servlet(): String {
        val requestId = requestIdProvider.getRequestId()
        log.info("Servlet requestId: $requestId")
        return "Servlet OK: $requestId"
    }
}