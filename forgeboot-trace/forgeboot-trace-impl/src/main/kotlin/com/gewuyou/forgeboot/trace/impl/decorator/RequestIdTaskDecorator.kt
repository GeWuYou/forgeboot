package com.gewuyou.forgeboot.trace.impl.decorator

import com.gewuyou.forgeboot.trace.api.config.TraceProperties
import org.slf4j.MDC
import org.springframework.core.task.TaskDecorator

/**
 *请求ID任务装饰器
 *
 * @since 2025-03-17 16:57:54
 * @author gewuyou
 */
class RequestIdTaskDecorator(
    private val traceProperties: TraceProperties
) : TaskDecorator {
    override fun decorate(task: Runnable): Runnable {
        val requestIdMdcKey = traceProperties.requestIdMdcKey
        // 获取主线程 requestId
        val requestId = MDC.get(requestIdMdcKey)
        return Runnable {
            try {
                MDC.put(requestIdMdcKey, requestId)
                task.run()
            } finally {
                MDC.remove(requestIdMdcKey)
            }
        }
    }
}