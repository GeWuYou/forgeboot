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

package com.gewuyou.forgeboot.context.impl.coroutine

import com.gewuyou.forgeboot.context.ContextHolders
import com.gewuyou.forgeboot.context.api.ContextProcessor
import com.gewuyou.forgeboot.context.impl.element.CoroutineContextMapElement
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * 上下文感知的协程作用域实现类
 * 用于在协程中保持上下文信息的传递和一致性
 *
 * @property processors 上下文处理器列表，用于在协程中注入和清理上下文数据
 * @since 2025-07-17 17:31:31
 * @author gewuyou
 */
class ContextAwareCoroutineScope(
    private val processors: List<ContextProcessor>,
) : CoroutineScope {

    /**
     * 协程上下文配置，包含SupervisorJob和默认调度器
     * SupervisorJob确保子协程的独立生命周期
     * Dispatchers.Default适用于计算密集型任务
     */
    override val coroutineContext: CoroutineContext = SupervisorJob()

    /**
     * 启动一个带有上下文传播的协程
     *
     * @param dispatcher 协程调度器，默认使用Dispatchers.Default
     * @param block 协程执行的具体逻辑
     * @return 返回Job对象用于管理协程生命周期
     */
    fun launchWithContext(
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        block: suspend CoroutineScope.() -> Unit,
    ): Job {
        val snapshot = ContextHolders.snapshot().toMutableMap()
        return launch(dispatcher + CoroutineContextMapElement(snapshot)) {
            try {
                // 注入上下文数据到当前协程
                processors.forEach { it.inject(Unit, snapshot) }
                block()
            } finally {
                // 清理上下文数据并释放资源
                processors.forEach { it.inject(Unit, mutableMapOf()) }
                ContextHolders.clear()
            }
        }
    }

    /**
     * 异步启动一个带有上下文传播的协程
     *
     * @param dispatcher 协程调度器，默认使用Dispatchers.Default
     * @param block 协程执行的具体逻辑
     * @return 返回Deferred对象用于获取异步执行结果
     */
    fun <T> asyncWithContext(
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        block: suspend CoroutineScope.() -> T,
    ): Deferred<T> {
        val snapshot = ContextHolders.snapshot().toMutableMap()
        return async(dispatcher + CoroutineContextMapElement(snapshot)) {
            try {
                // 注入上下文数据到当前协程
                processors.forEach { it.inject(Unit, snapshot) }
                block()
            } finally {
                // 清理上下文数据并释放资源
                processors.forEach { it.inject(Unit, mutableMapOf()) }
                ContextHolders.clear()
            }
        }
    }

    /**
     * 在指定上下文中执行挂起代码块
     *
     * @param block 需要执行的挂起代码块
     * @return 返回执行结果
     */
    suspend fun <T> runWithContext(
        block: suspend () -> T,
    ): T {
        val snapshot = ContextHolders.snapshot().toMutableMap()
        return withContext(CoroutineContextMapElement(snapshot)) {
            try {
                // 注入上下文数据到当前协程
                processors.forEach { it.inject(Unit, snapshot) }
                block()
            } finally {
                // 清理上下文数据并释放资源
                processors.forEach { it.inject(Unit, mutableMapOf()) }
                ContextHolders.clear()
            }
        }
    }
}