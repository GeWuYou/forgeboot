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

package com.gewuyou.forgeboot.safeguard.core.factory

import com.gewuyou.forgeboot.safeguard.core.model.RateLimitContext

/**
 * 限流异常工厂
 *
 * 用于创建限流相关的异常实例，实现ExceptionFactory接口专门处理RateLimitContext上下文
 *
 * @since 2025-09-23 21:39:43
 * @author gewuyou
 */
interface RateLimitExceptionFactory : ExceptionFactory<RateLimitContext> {

    /**
     * 根据限流上下文创建运行时异常
     *
     * @param ctx 限流上下文，包含限流相关的信息
     * @return 创建的运行时异常实例
     */
    override fun create(ctx: RateLimitContext): RuntimeException
}
