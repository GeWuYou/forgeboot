package com.gewuyou.forgeboot.context.impl.utils

import com.gewuyou.forgeboot.context.api.ContextProcessor
import com.gewuyou.forgeboot.context.impl.ContextHolder
import com.gewuyou.forgeboot.context.impl.coroutine.ContextAwareCoroutineScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 * 协程上下文工具类，用于在特定上下文中启动协程任务。
 *
 * @since 2025-07-18 11:17:44
 * @author gewuyou
 */
object CoroutineContextUtils {
    /**
     * 在指定的上下文中启动一个协程任务，并返回对应的 Job 对象。
     *
     * @param contextHolder 上下文持有者，用于管理上下文数据的生命周期。
     * @param processors 上下文处理器列表，用于处理上下文相关的逻辑。
     * @param dispatcher 协程调度器，决定协程在哪个线程或线程池中执行，默认为 [Dispatchers.Default]。
     * @param block 要执行的协程代码块，接收一个挂起的 [CoroutineScope] 扩展函数。
     *
     * @return 返回一个 [Job] 对象，可用于取消或跟踪协程任务的状态。
     */
    fun launchWithScopedContext(
        contextHolder: ContextHolder,
        processors: List<ContextProcessor>,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        block: suspend CoroutineScope.() -> Unit,
    ): Job {
        return ContextAwareCoroutineScope(contextHolder, processors).launchWithContext(dispatcher, block)
    }
}