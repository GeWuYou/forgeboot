package com.gewuyou.forgeboot.context.impl.element

import kotlinx.coroutines.ThreadContextElement
import org.slf4j.MDC
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * MDC上下文元素，用于在协程中传播MDC（Mapped Diagnostic Context）上下文。
 *
 * MDC 是 SLF4J 提供的一个日志诊断工具，允许将特定信息绑定到当前线程的上下文中，
 * 以便在日志输出时能够包含这些信息。由于协程可能在线程间切换，因此需要此实现来保证上下文一致性。
 *
 * @param contextMap 包含 MDC 上下文键值对的不可变 Map，用于保存和恢复诊断上下文。
 * @since 2025-07-16 11:05:47
 * @author gewuyou
 */
class MdcContextElement(
    private val contextMap: Map<String, String>
) : ThreadContextElement<Map<String, String>>,
    AbstractCoroutineContextElement(Key) {

    /**
     * 协程上下文键对象，用于标识此类上下文元素的唯一性。
     */
    companion object Key : CoroutineContext.Key<MdcContextElement>

    /**
     * 更新当前线程的 MDC 上下文为协程指定的上下文，并返回旧的上下文状态。
     *
     * 此方法会在协程切换至新线程时调用，以确保目标线程的 MDC 上下文与协程一致。
     *
     * @param context 当前协程的上下文，不直接使用但保留用于扩展。
     * @return 返回更新前的 MDC 上下文状态，用于后续恢复。
     */
    override fun updateThreadContext(context: CoroutineContext): Map<String, String> {
        val oldState = MDC.getCopyOfContextMap() ?: emptyMap()
        MDC.setContextMap(contextMap)
        return oldState
    }

    /**
     * 恢复当前线程的 MDC 上下文至先前保存的状态。
     *
     * 此方法在协程完成执行并释放线程资源时调用，确保线程可以还原其原始 MDC 上下文。
     *
     * @param context 当前协程的上下文，不直接使用但保留用于扩展。
     * @param oldState 需要恢复的先前 MDC 上下文状态。
     */
    override fun restoreThreadContext(context: CoroutineContext, oldState: Map<String, String>) {
        MDC.setContextMap(oldState)
    }
}