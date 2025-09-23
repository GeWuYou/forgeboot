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

package com.gewuyou.forgeboot.safeguard.demo.factory

import com.gewuyou.forgeboot.safeguard.core.factory.CooldownExceptionFactory
import com.gewuyou.forgeboot.safeguard.core.model.CooldownContext

/**
 *自定义冷却异常工厂
 *
 * @since 2025-09-23 13:08:01
 * @author gewuyou
 */
class CustomizeCooldownExceptionFactory : CooldownExceptionFactory {
    override fun create(ctx: CooldownContext): RuntimeException {
        return RuntimeException("自定义冷却异常")
    }
}