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

package com.gewuyou.forgeboot.safeguard.redis.cooldown

import com.gewuyou.forgeboot.safeguard.core.api.CooldownGuard
import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.model.CooldownTicket
import com.gewuyou.forgeboot.safeguard.core.policy.CooldownPolicy
import com.gewuyou.forgeboot.safeguard.redis.key.RedisKeyBuilder
import org.springframework.data.redis.core.StringRedisTemplate
import java.util.concurrent.TimeUnit

/**
 * Redis冷却防护器实现类
 * 用于基于Redis的分布式冷却控制，防止重复操作
 *
 * @property stringRedis Redis字符串操作模板
 * @property keyBuilder Redis键构建器
 * @since 2025-09-21 12:26:36
 * @author gewuyou
 */
class RedisCooldownGuard(
    private val stringRedis: StringRedisTemplate,
    private val keyBuilder: RedisKeyBuilder,
) : CooldownGuard {

    /**
     * 获取冷却令牌
     * 通过在Redis中设置一个带有过期时间的键来实现冷却控制
     *
     * @param key 冷却键标识
     * @param policy 冷却策略，包含过期时间等配置
     * @return 冷却令牌，包含获取结果和剩余时间
     */
    override fun acquire(key: Key, policy: CooldownPolicy): CooldownTicket {
        val redisKey = keyBuilder.cooldown(key)
        // 使用Redis的SETNX命令实现分布式锁，确保同一时间只有一个操作能获得令牌
        val ok = stringRedis.opsForValue().setIfAbsent(redisKey, "1", policy.ttl) ?: false
        // 获取键的剩余过期时间，用于计算冷却剩余时间
        val remainingMs = when (val ttlMs = stringRedis.getExpire(redisKey, TimeUnit.MILLISECONDS)) {
            -2L -> if (ok) policy.ttl.toMillis() else 0L
            // 极端并发下的兜底
            else -> ttlMs
        }
        return CooldownTicket(acquired = ok, remainingMillis = remainingMs)
    }

    /**
     * 释放冷却令牌
     * 删除Redis中的冷却键，解除冷却状态
     *
     * @param key 冷却键标识
     */
    override fun release(key: Key) {
        stringRedis.delete(keyBuilder.cooldown(key))
    }
}
