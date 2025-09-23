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

import com.gewuyou.forgeboot.safeguard.core.exception.IdempotencyConflictException
import com.gewuyou.forgeboot.safeguard.core.model.IdempotencyContext

/**
 *默认的掌声冲突异常工厂
 *
 * @since 2025-09-23 21:16:26
 * @author gewuyou
 */
class DefaultIdempotencyExceptionFactory : IdempotencyExceptionFactory {
    /**
     * 根据幂等性上下文创建运行时异常
     *
     * @param ctx 幂等性上下文，包含异常创建所需的信息
     * @return 创建的运行时异常实例
     */
    override fun create(ctx: IdempotencyContext): RuntimeException {
        return IdempotencyConflictException(ctx.key)
    }
}