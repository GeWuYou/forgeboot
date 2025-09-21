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

package com.gewuyou.forgeboot.safeguard.core.api

import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.model.CooldownTicket
import com.gewuyou.forgeboot.safeguard.core.policy.CooldownPolicy

/**
 * 冷却守卫接口，用于控制操作的冷却期管理
 *
 * @since 2025-09-21 11:39:55
 * @author gewuyou
 */
interface CooldownGuard {
    /**
     * 获取冷却令牌，采用Set if Not Exists语义
     * 当成功获取令牌时，表示可以执行操作；失败则表示仍处于冷却期
     *
     * @param key 冷却控制的键值，用于标识不同的冷却域
     * @param policy 冷却策略，定义冷却时间和相关规则
     * @return CooldownTicket 冷却令牌，用于后续的状态管理
     */
    fun acquire(key: Key, policy: CooldownPolicy): CooldownTicket

    /**
     * 释放冷却令牌，用于业务执行失败时回滚冷却状态
     * 使当前键值重新进入可获取状态
     *
     * @param key 冷却控制的键值，用于标识需要回滚的冷却域
     */
    fun release(key: Key)
}
