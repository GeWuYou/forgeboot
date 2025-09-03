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

package com.gewuyou.forgeboot.webmvc.logger.api.entities

import com.gewuyou.forgeboot.context.impl.coroutine.ContextAwareCoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * 协程日志对象，用于在协程环境中安全地记录日志
 *
 * @since 2025-04-26 19:20:43
 * @author gewuyou
 */
object CoroutineLogger {
    /**
     * 协程作用域，用于启动日志记录协程
     * 使用@Volatile注解确保多线程环境下的可见性
     */
    @Volatile
    private var scope: ContextAwareCoroutineScope? = null

    /**
     * 初始化协程日志记录器的作用域
     *
     * @param scope 协程作用域，如果为null则创建默认作用域使用SupervisorJob和IO调度器
     */
    fun init(scope: ContextAwareCoroutineScope) {
        this.scope = scope
    }

    /**
     * 安全地执行日志记录block，异常会被捕获并忽略，确保日志记录的稳定性
     *
     * @param block 日志记录的协程block，包含实际的日志记录逻辑
     */
    fun safeLog(block: suspend () -> Unit) {
        // 在协程作用域中启动一个新的协程来执行日志记录任务
        scope?.launchWithContext(
            dispatcher = Dispatchers.IO
        ) {
            try {
                // 尝试执行日志记录block
                block()
            } catch (_: Exception) {
                // 吞噬日志错误，确保协程稳定运行，不会因单个日志记录失败而崩溃
            }
        }
    }
}

