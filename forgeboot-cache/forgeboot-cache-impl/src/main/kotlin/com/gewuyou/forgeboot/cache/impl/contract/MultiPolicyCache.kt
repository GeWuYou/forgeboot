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

package com.gewuyou.forgeboot.cache.impl.contract

import com.gewuyou.forgeboot.cache.api.contract.Cache
import com.gewuyou.forgeboot.cache.api.policy.CachePolicy
import com.gewuyou.forgeboot.cache.api.policy.NullValuePolicy
import java.time.Duration

/**
 * 多策略缓存装饰器
 *
 * 支持多个缓存策略，动态应用各种缓存策略（如 Null 值策略、失效策略等）。
 * 该类通过组合多个 CachePolicy 实现对基础缓存操作的增强处理。
 *
 * @property delegate 被包装的真实缓存实现，用于执行底层缓存操作
 * @property policies 缓存策略列表，按顺序依次应用于缓存值
 * @since 2025-06-17 21:16:58
 */
class MultiPolicyCache(
    private val delegate: Cache,
    private val nullValuePolicy: NullValuePolicy,
    private val policies: List<CachePolicy>,
) : Cache by delegate {

    init {
        // 排序
        policies.sortedBy { it.getOrder() }
    }

    /**
     * 获取缓存值并应用所有缓存策略
     *
     * 按照策略列表顺序依次处理获取到的缓存值：
     * - 如果原始值为 null 或 null 占位符，则返回 null
     * - 否则返回经过策略处理后的最终值
     *
     * @param key 缓存键，用于定位缓存项
     * @return 经过策略处理后的缓存值，如果为 null 或 null 占位符则返回 null
     */
    override fun retrieve(key: String): String? {
        var value = delegate.retrieve(key)
        // 按策略顺序依次处理缓存值
        policies.forEach { value = it.apply(key, value) }
        return value
    }

    /**
     * 存储缓存值并应用所有缓存策略
     *
     * 按照策略列表顺序依次处理要存储的值：
     * - 如果值为 null，可能被转换为 null 占位符
     * - 最终将处理后的值委托给底层缓存实现进行存储
     *
     * @param key 缓存键，用于标识要存储的缓存项
     * @param value 要存储的值，可能为 null
     * @param ttl 缓存过期时间，控制该项在缓存中的存活时长
     */
    override fun put(key: String, value: String?, ttl: Duration) {
        var processedValue = value
        // 按策略顺序依次处理待存储的值
        policies.forEach { processedValue = it.apply(key, processedValue) }
        // 如果最终值是 null 且不允许缓存 null，跳过缓存存储
        if (processedValue == null && !nullValuePolicy.allowCacheNull(key)) {
            // 不缓存 null 值
            return
        }
        delegate.put(key, processedValue, ttl)
    }
}
