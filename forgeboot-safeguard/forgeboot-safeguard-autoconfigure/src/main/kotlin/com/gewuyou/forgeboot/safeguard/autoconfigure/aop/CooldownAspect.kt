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

package com.gewuyou.forgeboot.safeguard.autoconfigure.aop

import com.gewuyou.forgeboot.safeguard.autoconfigure.annotations.Cooldown
import com.gewuyou.forgeboot.safeguard.autoconfigure.key.KeyResolutionSupport
import com.gewuyou.forgeboot.safeguard.autoconfigure.spel.SpelEval
import com.gewuyou.forgeboot.safeguard.core.api.CooldownGuard
import com.gewuyou.forgeboot.safeguard.core.exception.CooldownActiveException
import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.metrics.NoopSafeguardMetrics
import com.gewuyou.forgeboot.safeguard.core.metrics.SafeguardMetrics
import com.gewuyou.forgeboot.safeguard.core.policy.CooldownPolicy
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.beans.factory.BeanFactory
import org.springframework.core.annotation.Order
import java.time.Duration

/**
 * 冷却注解切面类，用于处理方法级别的冷却控制逻辑
 *
 * @param guard 冷却守卫实例，负责具体的冷却控制
 * @param beanFactory Spring Bean工厂，用于表达式求值
 * @since 2025-09-21 14:15:08
 * @author gewuyou
 */
@Aspect
@Order(0)
class CooldownAspect(
    private val guard: CooldownGuard,
    private val beanFactory: BeanFactory,
    private val metrics: SafeguardMetrics = NoopSafeguardMetrics,
    private val keySupport: KeyResolutionSupport,
) {

    private companion object {
        const val NS = "safeguard:cooldown"
    }

    /**
     * 环绕通知方法，处理带有Cooldown注解的方法执行
     *
     * @param pjp 连接点对象，包含被拦截方法的信息
     * @param cd 冷却注解对象，包含冷却配置信息
     * @return 被拦截方法的执行结果
     * @throws CooldownActiveException 当冷却期间重复调用时抛出
     */
    @Around("@annotation(cd)")
    fun around(pjp: ProceedingJoinPoint, cd: Cooldown): Any? {
        // 1) 解析键与持续时间
        val secs = SpelEval.eval(pjp, beanFactory, cd.seconds, Number::class.java).toLong()
        val key: Key = keySupport.resolveForPjp(
            pjp, cd.resolverBean, cd.template, cd.key, defaultNamespace = NS
        )
        val policy = CooldownPolicy(Duration.ofSeconds(secs))

        // 2) 尝试获取冷却锁
        val acquired = guard.acquire(key, policy).acquired
        if (!acquired) {
            // —— 埋点：冷却期内被拦截
            metrics.onCooldownBlocked(key.namespace, key.value)
            throw CooldownActiveException(key)
        }

        // 3) 执行业务；若命中回滚异常则提前释放冷却锁
        return try {
            pjp.proceed()
        } catch (ex: Throwable) {
            if (cd.rollbackOn.any { it.java.isAssignableFrom(ex.javaClass) }) {
                guard.release(key)
                // —— 埋点：因异常回滚释放
                metrics.onCooldownRolledBack(key.namespace, key.value)
            }
            throw ex
        }
    }
}
