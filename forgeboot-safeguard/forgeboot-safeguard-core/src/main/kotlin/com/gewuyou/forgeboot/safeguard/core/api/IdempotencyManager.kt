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

import com.gewuyou.forgeboot.safeguard.core.enums.IdempotencyStatus
import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.model.IdempotencyRecord
import com.gewuyou.forgeboot.safeguard.core.policy.IdempotencyPolicy

/**
 * 幂等性管理器接口，用于处理幂等性相关的操作
 *
 * @since 2025-09-21 11:42:02
 * @author gewuyou
 */
interface IdempotencyManager {
    /**
     * 读取指定键的幂等性记录
     *
     * @param key 幂等性键值，用于唯一标识一个操作
     * @return 幂等性记录，如果不存在则返回 null
     */
    operator fun get(key: Key): IdempotencyRecord?

    /**
     * 尝试获取指定键的_PENDING_状态锁
     *
     * @param key 幂等性键值，用于唯一标识一个操作
     * @param policy 幂等性策略，定义了操作的行为规则
     * @return true表示成功获取锁，false表示已存在记录无法获取锁
     */
    fun tryAcquirePending(key: Key, policy: IdempotencyPolicy): Boolean

    /**
     * 保存成功的幂等性记录并更新TTL
     *
     * @param key 幂等性键值，用于唯一标识一个操作
     * @param record 幂等性记录对象，包含操作结果等信息
     * @param policy 幂等性策略，定义了操作的行为规则
     */
    fun saveSuccess(key: Key, record: IdempotencyRecord, policy: IdempotencyPolicy)

    /**
     * 清除指定键的幂等性记录（通常用于失败回滚）
     *
     * @param key 幂等性键值，用于唯一标识一个操作
     */
    fun clear(key: Key)

    /**
     * 更新指定键的幂等性状态
     *
     * @param key 幂等性键值，用于唯一标识一个操作
     * @param status 新的幂等性状态
     * @param policy 幂等性策略，定义了操作的行为规则
     */
    fun updateStatus(key: Key, status: IdempotencyStatus, policy: IdempotencyPolicy)
}
