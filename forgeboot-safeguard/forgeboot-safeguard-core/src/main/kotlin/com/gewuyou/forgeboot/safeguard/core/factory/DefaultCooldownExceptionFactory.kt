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

import com.gewuyou.forgeboot.safeguard.core.exception.CooldownActiveException
import com.gewuyou.forgeboot.safeguard.core.model.CooldownContext

/**
 * 默认冷却异常工厂
 *
 * @since 2025-09-23 10:41:15
 * @author gewuyou
 */
class DefaultCooldownExceptionFactory : CooldownExceptionFactory {
    /**
     * 创建冷却异常实例
     *
     * @param ctx 冷却上下文，包含异常相关信息
     * @return RuntimeException 冷却激活异常实例
     */
    override fun create(ctx: CooldownContext): RuntimeException =
        CooldownActiveException(key = ctx.key)
}
