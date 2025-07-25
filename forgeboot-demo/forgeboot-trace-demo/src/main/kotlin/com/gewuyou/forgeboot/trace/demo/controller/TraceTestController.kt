package com.gewuyou.forgeboot.trace.demo.controller

import com.gewuyou.forgeboot.context.api.ContextProcessor
import com.gewuyou.forgeboot.context.impl.ContextHolder
import com.gewuyou.forgeboot.context.impl.coroutine.ContextAwareCoroutineScope
import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.trace.api.RequestIdProvider
import com.gewuyou.forgeboot.webmvc.dto.R
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
    suspend fun coroutine(): R<String> {
        val requestId = requestIdProvider.getRequestId()
        log.info("→ Controller RequestId: $requestId")

        // 模拟内部异步任务（3秒后完成）
        val scope = ContextAwareCoroutineScope(contextHolder,processors)
        scope.launchWithContext {
            log.info("RID: ${requestIdProvider.getRequestId()}")
        }
        return R.success("Main coroutine returned immediately with requestId: $requestId",requestIdProvider = requestIdProvider)
    }

    @GetMapping("/servlet")
    fun servlet(): String {
        val requestId = requestIdProvider.getRequestId()
        log.info("Servlet requestId: $requestId")
        return "Servlet OK: $requestId"
    }
}