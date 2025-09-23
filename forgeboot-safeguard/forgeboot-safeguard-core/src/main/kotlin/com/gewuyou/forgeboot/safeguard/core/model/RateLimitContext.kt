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

package com.gewuyou.forgeboot.safeguard.core.model

import com.gewuyou.forgeboot.safeguard.core.key.Key
import org.aspectj.lang.ProceedingJoinPoint
import java.lang.reflect.Method
import java.time.Instant

/**
 *限流上下文
 *
 * @since 2025-09-23 20:29:43
 * @author gewuyou
 */
data class RateLimitContext(
    override val key: Key,
    override val scene: String?,
    override val infoCode: String?,
    override val joinPoint: ProceedingJoinPoint,
    override val method: Method,
    override val now: Instant,
    override val args: Array<Any?>,
    override val currentAnnotation: Annotation,
    override val annotations: Array<Annotation>,
) : GuardContext(
    key,
    scene,
    infoCode,
    joinPoint,
    method,
    now,
    args,
    currentAnnotation,
    annotations

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RateLimitContext) return false

        if (key != other.key) return false
        if (scene != other.scene) return false
        if (infoCode != other.infoCode) return false
        if (joinPoint != other.joinPoint) return false
        if (method != other.method) return false
        if (now != other.now) return false
        if (!args.contentEquals(other.args)) return false
        if (currentAnnotation != other.currentAnnotation) return false
        if (!annotations.contentEquals(other.annotations)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + (scene?.hashCode() ?: 0)
        result = 31 * result + (infoCode?.hashCode() ?: 0)
        result = 31 * result + joinPoint.hashCode()
        result = 31 * result + method.hashCode()
        result = 31 * result + now.hashCode()
        result = 31 * result + args.contentHashCode()
        result = 31 * result + currentAnnotation.hashCode()
        result = 31 * result + annotations.contentHashCode()
        return result
    }
}