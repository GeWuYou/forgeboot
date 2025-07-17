package com.gewuyou.forgeboot.context.impl.element

import com.gewuyou.forgeboot.context.impl.ContextHolder
import kotlinx.coroutines.ThreadContextElement
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * 协程上下文键值对元素，用于在协程上下文中保存和恢复一个Map结构的数据
 *
 * @property contextMap 保存协程上下文数据的Map，键为字符串，值为可空的任意类型对象
 * @since 2025-07-17 14:27:05
 * @author gewuyou
 */
class CoroutineContextMapElement(
    private val contextHolder: ContextHolder,
    private val contextMap: Map<String, Any?>,
) : ThreadContextElement<Map<String, Any?>>,
    AbstractCoroutineContextElement(Key) {

    companion object Key : CoroutineContext.Key<CoroutineContextMapElement> {

        /**
         * 在统一的协程上下文快照中执行指定的挂起代码块。
         *
         * 该方法会将给定的上下文快照设置为新的协程上下文，并在其中执行传入的代码块。
         * 使用 [withContext] 切换到新的协程上下文，同时通过 [ContextHolder.putAll] 更新上下文数据。
         *
         * @param contextHolder 用于存储和更新上下文数据的容器
         * @param snapshot 提供初始上下文数据的键值对映射
         * @param block 需要在新上下文中执行的挂起函数
         * @return 返回执行完成后的结果
         */
        suspend fun <T> withUnifiedContextSnapshot(
            contextHolder: ContextHolder,
            snapshot: Map<String, Any?>,
            block: suspend () -> T,
        ): T {
            return withContext(CoroutineContextMapElement(contextHolder, snapshot)) {
                contextHolder.putAll(snapshot)
                block()
            }
        }
    }

    /**
     * 更新当前协程上下文时调用，保存当前上下文数据
     *
     * @param context 当前协程上下文
     * @return 返回一个空Map，表示保存的上下文状态
     */
    override fun updateThreadContext(context: CoroutineContext): Map<String, Any?> {
        val oldContext = contextHolder.snapshot() // 原始 ThreadLocal 快照
        contextMap.forEach { (k, v) ->
            if (v != null) contextHolder.put(k, v.toString())
        }
        return oldContext
    }

    /**
     * 恢复协程上下文时调用，用于将之前保存的上下文状态还原
     *
     * @param context 当前协程上下文
     * @param oldState 之前保存的上下文状态
     */
    override fun restoreThreadContext(context: CoroutineContext, oldState: Map<String, Any?>) {
        contextHolder.clear()
        oldState.forEach { (k, v): Map.Entry<String, Any?> -> contextHolder.put(k, v) }
    }

    /**
     * 获取当前协程上下文中保存的Map数据
     *
     * @return 返回包含当前上下文数据的Map
     */
    fun getContext(): Map<String, Any?> = contextMap


}