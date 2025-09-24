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

package com.gewuyou.forgeboot.safeguard.core.exception

import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.model.IdempotencyRecord


/**
 * 异常基类，用于表示各种保护机制触发的异常情况
 *
 * @property message 异常的详细描述信息
 * @property key 触发异常的键值对象
 * @property code 异常代码，用于标识异常类型
 * @since 2025-09-21 11:37:11
 * @author gewuyou
 */
sealed class SafeguardException(
    message: String,
    val key: Key,
    val code: String,
) : RuntimeException(message)

/**
 * 已有记录异常类，用于在检测到已有成功记录时提前返回结果。
 *
 * @property key 触发异常的键值对象
 * @property record 已存在的幂等记录
 */
class IdempotencyReturnValueFromRecordException(
    key: Key,
    val record: IdempotencyRecord,
) : SafeguardException("Return value from record : ${key.full()}", key, "RETURN_VALUE_FROM_RECORD")

/**
 * 速率限制异常，当请求超过设定的速率限制时抛出
 *
 * @property key 触发速率限制的键值对象
 */
class RateLimitExceededException(key: Key) :
    SafeguardException("Rate limit exceeded: ${key.full()}", key, "RATE_LIMIT_EXCEEDED")

/**
 * 冷却期异常，当操作仍在冷却期内时抛出
 *
 * @property key 触发冷却期限制的键值对象
 */
class CooldownActiveException(key: Key) :
    SafeguardException("Cooldown active: ${key.full()}", key, "COOLDOWN_ACTIVE")

/**
 * 幂等性冲突异常，当违反幂等性约束时抛出
 *
 * @property key 触发幂等性检查的键值对象
 */
class IdempotencyConflictException(key: Key) :
    SafeguardException("Idempotency conflict: ${key.full()}", key, "IDEMPOTENCY_CONFLICT")

/**
 * 当尝试次数超过限制时抛出的异常类
 *
 * @param key 触发异常的键值对象，用于标识具体的限制项
 */
class AttemptLimitExceededException(key: Key) :
    SafeguardException("Attempt limit exceeded: ${key.full()}", key, "ATTEMPT_LIMIT_EXCEEDED")

