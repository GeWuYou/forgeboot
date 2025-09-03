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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 协程日志对象，用于在协程环境中安全地记录日志
 *
 * @since 2025-04-26 19:20:43
 * @author gewuyou
 */
object CoroutineLogger {
    // 创建一个协程作用域，使用SupervisorJob和IO调度器，适合进行IO密集型的日志记录任务
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * 安全地执行日志记录block，异常会被捕获并忽略，确保日志记录的稳定性
     *
     * @param block 日志记录的协程block，包含实际的日志记录逻辑
     */
    fun safeLog(block: suspend () -> Unit) {
        // 在协程作用域中启动一个新的协程来执行日志记录任务
        scope.launch {
            try {
                // 尝试执行日志记录block
                block()
            } catch (ex: Exception) {
                // 吞噬日志错误，确保协程稳定运行，不会因单个日志记录失败而崩溃
            }
        }
    }
}
