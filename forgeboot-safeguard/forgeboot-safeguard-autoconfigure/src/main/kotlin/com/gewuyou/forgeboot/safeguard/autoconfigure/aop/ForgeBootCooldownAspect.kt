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
import com.gewuyou.forgeboot.safeguard.autoconfigure.resolver.CooldownExceptionFactoryResolver
import com.gewuyou.forgeboot.safeguard.autoconfigure.spel.SpelEval
import com.gewuyou.forgeboot.safeguard.core.api.CooldownGuard
import com.gewuyou.forgeboot.safeguard.core.exception.CooldownActiveException
import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.metrics.NoopSafeguardMetrics
import com.gewuyou.forgeboot.safeguard.core.metrics.SafeguardMetrics
import com.gewuyou.forgeboot.safeguard.core.model.CooldownContext
import com.gewuyou.forgeboot.safeguard.core.policy.CooldownPolicy
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.beans.factory.BeanFactory
import org.springframework.core.annotation.Order
import java.time.Duration
import java.time.Instant

/**
 * 冷却注解切面类，用于处理方法级别的冷却控制逻辑
 *
 * @param guard 冷却守卫实例，负责具体的冷却控制
 * @param beanFactory Spring Bean工厂，用于表达式求值
 * @param metrics 安全防护指标收集器，用于记录冷却相关指标，默认为无操作实现
 * @param keySupport 键解析支持类，用于解析冷却键
 * @param resolver 注解异常工厂解析器
 * @since 2025-09-21 14:15:08
 * @author gewuyou
 */
@Aspect
@Order(0)
class ForgeBootCooldownAspect(
    private val guard: CooldownGuard,
    private val beanFactory: BeanFactory,
    private val metrics: SafeguardMetrics = NoopSafeguardMetrics,
    private val keySupport: KeyResolutionSupport,
    private val resolver: CooldownExceptionFactoryResolver,
) {

    private companion object {
        const val NS = "safeguard:cooldown"
    }

    /**
     * 环绕通知方法，处理带有Cooldown注解的方法执行
     *
     * 该方法会在目标方法执行前后进行拦截，完成以下操作：
     * 1. 解析冷却键与持续时间；
     * 2. 尝试获取冷却锁，若未获取到则抛出异常；
     * 3. 执行目标方法，若命中回滚异常则提前释放冷却锁。
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
        // 获取方法对象
        val methodSignature = pjp.signature as MethodSignature
        val method = methodSignature.method
        // 2) 尝试获取冷却锁
        val acquired = guard.acquire(key, policy)
        val context = CooldownContext(
            key,
            acquired.remainingMillis,
            cd.scene,
            cd.infoCode,
            pjp,
            method,
            Instant.now(),
            pjp.args,
            method.annotations,
            cd
        )
        if (!acquired.acquired) {
            // —— 埋点：冷却期内被拦截
            metrics.onCooldownBlocked(key.namespace, key.value)
            throw constructException(cd, context)
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

    /**
     * 构造冷却异常实例
     *
     * @param cd 冷却注解对象，包含异常工厂信息
     * @param context 冷却上下文，包含异常构造所需数据
     * @return 构造好的运行时异常实例
     */
    private fun constructException(
        cd: Cooldown,
        context: CooldownContext,
    ): RuntimeException {
        val factory = resolver.resolve(cd)
        return factory.create(context)
    }
}
