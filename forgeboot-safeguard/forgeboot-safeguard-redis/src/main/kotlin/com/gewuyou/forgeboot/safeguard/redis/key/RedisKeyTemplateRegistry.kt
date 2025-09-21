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

package com.gewuyou.forgeboot.safeguard.redis.key

import com.gewuyou.forgeboot.safeguard.core.key.KeyTemplate
import com.gewuyou.forgeboot.safeguard.core.key.KeyTemplateRegistry
import com.gewuyou.forgeboot.safeguard.redis.config.SafeguardProperties
import org.springframework.data.redis.core.StringRedisTemplate

/**
 * Redis密钥模板注册表
 *
 * @since 2025-09-21 19:12:29
 * @author gewuyou
 */
class RedisKeyTemplateRegistry(
    private val redis: StringRedisTemplate,
    private val safeguardProperties: SafeguardProperties,
) : KeyTemplateRegistry {

    private val indexKey = "${safeguardProperties.redisKeyTemplatePrefix}:index"
    private fun keyOf(name: String) = "${safeguardProperties.redisKeyTemplatePrefix}:$name"

    companion object {
        const val NS_HASH_KEY = "ns"
        const val PT_HASH_KEY = "pt"
    }

    /**
     * 注册一个密钥模板
     *
     * 将给定的模板信息存储到 Redis 中，并将其名称添加到索引集合中，
     * 以便后续可以快速检索和遍历所有已注册的模板。
     *
     * @param name 模板名称，用于唯一标识该模板
     * @param template 要注册的密钥模板对象，包含命名空间和模式信息
     */
    override fun register(name: String, template: KeyTemplate) {
        val hashOps = redis.opsForHash<String, String>()
        hashOps.put(keyOf(name), NS_HASH_KEY, template.namespace)
        hashOps.put(keyOf(name), PT_HASH_KEY, template.pattern)
        redis.opsForSet().add(indexKey, name)
    }

    /**
     * 根据名称获取已注册的密钥模板
     *
     * 从 Redis 中读取指定名称对应的模板信息。如果模板不存在或数据不完整，
     * 则返回 null。
     *
     * @param name 模板名称
     * @return 对应的密钥模板对象，如果未找到或数据不完整则返回 null
     */
    override fun get(name: String): KeyTemplate? {
        val entries = redis.opsForHash<String, String>().entries(keyOf(name))
        if (entries.isEmpty()) return null
        val ns = entries[NS_HASH_KEY] ?: return null
        val pt = entries[PT_HASH_KEY] ?: return null
        return KeyTemplate(ns, pt)
    }

    /**
     * 获取所有已注册的密钥模板
     *
     * 遍历索引集合中的所有模板名称，并从 Redis 中读取每个模板的具体信息。
     * 若发现无效或损坏的数据条目，则会自动清理其在索引中的记录。
     *
     * @return 包含所有有效模板的映射表，键为模板名称，值为对应的模板对象。
     *         若无有效模板，则返回空映射。
     */
    override fun all(): Map<String, KeyTemplate> {
        val names = redis.opsForSet().members(indexKey) ?: emptySet()
        if (names.isEmpty()) return emptyMap()

        val map = LinkedHashMap<String, KeyTemplate>(names.size)
        val hashOps = redis.opsForHash<String, String>()

        // 简洁实现：逐个读；数量大时可改 executePipelined 优化
        for (n in names) {
            val e = hashOps.entries(keyOf(n))
            val ns = e[NS_HASH_KEY]
            val pt = e[PT_HASH_KEY]
            if (ns != null && pt != null) {
                map[n] = KeyTemplate(ns, pt)
            } else {
                // 清理脏索引
                redis.opsForSet().remove(indexKey, n)
            }
        }
        return map
    }
}
