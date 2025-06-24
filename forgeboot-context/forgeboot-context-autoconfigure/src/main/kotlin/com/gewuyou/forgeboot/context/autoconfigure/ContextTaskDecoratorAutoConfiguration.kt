package com.gewuyou.forgeboot.context.autoconfigure

import com.gewuyou.forgeboot.context.api.ContextProcessor
import com.gewuyou.forgeboot.context.impl.ContextHolder
import org.springframework.context.annotation.Bean
import org.springframework.core.task.TaskDecorator
import kotlin.collections.forEach

/**
 * Context Task Decorator 自动配置
 *
 * 用于自动装配一个 TaskDecorator Bean，确保异步任务执行时能够继承和保持上下文一致性。
 * 通过注入处理器链和上下文持有者，实现上下文的捕获、传递和清理。
 *
 * @since 2025-06-24 22:18:51
 * @author gewuyou
 */
class ContextTaskDecoratorAutoConfiguration {
    /**
     * 创建 TaskDecorator Bean，用于在异步执行中保持上下文一致性。
     *
     * 通过装饰线程池任务，确保异步任务继承调用线程的上下文状态。
     * 此方法会注入所有可用的 ContextProcessor 来处理上下文注入逻辑。
     *
     * @param processors 所有处理器列表，用于上下文注入操作
     * @param contextHolder 上下文持有者，用于获取和存储当前线程的上下文快照
     * @return 构建完成的 TaskDecorator 实例，用于装饰异步任务
     */
    @Bean
    fun contextTaskDecorator(processors: List<ContextProcessor>, contextHolder: ContextHolder) =
        TaskDecorator { delegate ->
            // 捕获当前线程的上下文快照，用于传递给异步任务
            val snap = contextHolder.snapshot()
            Runnable {
                try {
                    // 将快照内容重新放入当前线程的上下文持有者中
                    snap.forEach(contextHolder::put)
                    // 调用每个处理器的 inject 方法，将上下文数据注入到目标对象中
                    processors.forEach { it.inject(Unit, snap.toMutableMap()) }
                    // 执行被装饰的任务
                    delegate.run()
                } finally {
                    // 清理当前线程的上下文注入数据，避免内存泄漏
                    processors.forEach { it.inject(Unit, mutableMapOf()) }
                    // 清空上下文持有者的全部数据
                    contextHolder.clear()
                }
            }
        }
}