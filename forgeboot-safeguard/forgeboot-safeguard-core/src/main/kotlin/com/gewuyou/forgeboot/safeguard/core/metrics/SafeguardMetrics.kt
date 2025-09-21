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

package com.gewuyou.forgeboot.safeguard.core.metrics

/**
 * 防护指标接口，用于监控和统计防护相关的行为指标
 *
 * @since 2025-09-21 11:22:04
 * @author gewuyou
 */
interface SafeguardMetrics {
    /**
     * 当请求被限流拦截时调用
     *
     * @param namespace 命名空间，用于区分不同的业务场景
     * @param key 限流键，用于标识具体限流对象
     */
    fun onRateLimitBlocked(namespace: String, key: String) {
        // ignore
    }

    /**
     * 当请求被冷却期拦截时调用
     *
     * @param namespace 命名空间，用于区分不同的业务场景
     * @param key 冷却键，用于标识具体冷却对象
     */
    fun onCooldownBlocked(namespace: String, key: String) {
        // ignore
    }

    /**
     * 当命名空间和键的冷却时间被回滚时触发的回调函数
     *
     * @param namespace 命名空间字符串，用于标识冷却时间组
     * @param key 键字符串，用于标识具体的冷却时间项
     */
    fun onCooldownRolledBack(namespace: String, key: String) {
        // ignore
    }

    /**
     * 当幂等性检查命中缓存时调用
     *
     * @param namespace 命名空间，用于区分不同的业务场景
     * @param key 幂等键，用于标识具体幂等对象
     */
    fun onIdemHit(namespace: String, key: String) {
        // ignore
    }

    /**
     * 当幂等性检查未命中缓存时调用
     *
     * @param namespace 命名空间，用于区分不同的业务场景
     * @param key 幂等键，用于标识具体幂等对象
     */
    fun onIdemMiss(namespace: String, key: String) {
        // ignore
    }

    /**
     * 当幂等性检查发生冲突时调用
     *
     * @param namespace 命名空间，用于区分不同的业务场景
     * @param key 幂等键，用于标识具体幂等对象
     */
    fun onIdemConflict(namespace: String, key: String) {
        // ignore
    }
}

/**
 * 空实现的防护指标对象，不执行任何监控操作
 */
object NoopSafeguardMetrics : SafeguardMetrics
