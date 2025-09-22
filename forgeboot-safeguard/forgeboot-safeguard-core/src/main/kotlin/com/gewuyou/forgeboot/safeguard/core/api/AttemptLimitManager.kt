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
import com.gewuyou.forgeboot.safeguard.core.model.AttemptCheck
import com.gewuyou.forgeboot.safeguard.core.policy.AttemptPolicy

/**
 * 尝试限制管理器
 *
 * 用于管理尝试次数限制的接口，提供失败和成功情况下的尝试次数检查功能
 *
 * @since 2025-09-22 10:10:35
 * @author gewuyou
 */
interface AttemptLimitManager {
    /**
     * 处理失败情况下的尝试次数检查
     *
     * 当操作失败时调用此方法，用于更新和检查尝试次数限制
     *
     * @param key 操作的唯一标识键，用于区分不同的操作实例
     * @param policy 尝试策略，定义限制规则和行为
     * @return AttemptCheck 尝试检查结果，包含是否允许继续尝试等信息
     */
    fun onFail(key: Key, policy: AttemptPolicy): AttemptCheck

    /**
     * 处理成功情况下的尝试次数检查
     *
     * 当操作成功时调用此方法，用于重置或更新尝试次数状态
     *
     * @param key 操作的唯一标识键，用于区分不同的操作实例
     * @param policy 尝试策略，定义限制规则和行为
     * @return AttemptCheck 尝试检查结果，包含是否允许继续尝试等信息
     */
    fun onSuccess(key: Key, policy: AttemptPolicy): AttemptCheck

    /**
     * 检查指定键值是否符合给定的尝试策略
     *
     * @param key 需要检查的键值对象
     * @param policy 尝试策略，用于定义检查规则
     * @return 返回检查结果，包含是否通过检查以及相关的尝试信息
     */
    fun onCheck(key: Key, policy: AttemptPolicy): AttemptCheck
}
