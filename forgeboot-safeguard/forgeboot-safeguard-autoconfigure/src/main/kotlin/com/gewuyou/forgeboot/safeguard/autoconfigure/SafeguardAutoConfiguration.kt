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

package com.gewuyou.forgeboot.safeguard.autoconfigure

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.safeguard.autoconfigure.aop.AttemptLimitAspect
import com.gewuyou.forgeboot.safeguard.autoconfigure.aop.CooldownAspect
import com.gewuyou.forgeboot.safeguard.autoconfigure.aop.IdempotentAspect
import com.gewuyou.forgeboot.safeguard.autoconfigure.aop.RateLimitAspect
import com.gewuyou.forgeboot.safeguard.autoconfigure.key.KeyResolutionSupport
import com.gewuyou.forgeboot.safeguard.autoconfigure.resolver.AttemptLimitExceptionFactoryResolver
import com.gewuyou.forgeboot.safeguard.autoconfigure.resolver.CooldownExceptionFactoryResolver
import com.gewuyou.forgeboot.safeguard.autoconfigure.resolver.IdempotentExceptionFactoryResolver
import com.gewuyou.forgeboot.safeguard.autoconfigure.resolver.RateLimitExceptionFactoryResolver
import com.gewuyou.forgeboot.safeguard.autoconfigure.web.IdempotencyReturnAdvice
import com.gewuyou.forgeboot.safeguard.core.api.AttemptLimitManager
import com.gewuyou.forgeboot.safeguard.core.api.CooldownGuard
import com.gewuyou.forgeboot.safeguard.core.api.IdempotencyManager
import com.gewuyou.forgeboot.safeguard.core.api.RateLimiter
import com.gewuyou.forgeboot.safeguard.core.factory.*
import com.gewuyou.forgeboot.safeguard.core.key.KeyFactory
import com.gewuyou.forgeboot.safeguard.core.key.KeyTemplateRegistry
import com.gewuyou.forgeboot.safeguard.core.metrics.NoopSafeguardMetrics
import com.gewuyou.forgeboot.safeguard.core.metrics.SafeguardMetrics
import com.gewuyou.forgeboot.safeguard.core.serialize.PayloadCodec
import com.gewuyou.forgeboot.safeguard.redis.attemptlimit.Bucket4jAttemptLimitManager
import com.gewuyou.forgeboot.safeguard.redis.attemptlimit.LuaAttemptLimitManager
import com.gewuyou.forgeboot.safeguard.redis.codec.JacksonPayloadCodec
import com.gewuyou.forgeboot.safeguard.redis.config.SafeguardProperties
import com.gewuyou.forgeboot.safeguard.redis.cooldown.RedisCooldownGuard
import com.gewuyou.forgeboot.safeguard.redis.idem.RedisIdempotencyManager
import com.gewuyou.forgeboot.safeguard.redis.key.RedisKeyBuilder
import com.gewuyou.forgeboot.safeguard.redis.key.RedisKeyTemplateRegistry
import com.gewuyou.forgeboot.safeguard.redis.ratelimit.Bucket4jRateLimiter
import com.gewuyou.forgeboot.safeguard.redis.ratelimit.LuaRedisRateLimiter
import com.gewuyou.forgeboot.safeguard.redis.support.LuaScriptExecutor
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy
import io.github.bucket4j.distributed.proxy.ProxyManager
import io.github.bucket4j.distributed.serialization.Mapper
import io.github.bucket4j.redis.redisson.Bucket4jRedisson
import jakarta.servlet.http.HttpServletRequest
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.core.StringRedisTemplate
import java.time.Duration

/**
 * 自动配置类，用于根据条件自动装配限流、幂等、冷却等防护组件。
 *
 * @since 2025-09-21 14:10:05
 * @author gewuyou
 */
@AutoConfiguration
@EnableConfigurationProperties(SafeguardProperties::class)
class SafeguardAutoConfiguration {

    /**
     * 创建默认的尝试限制异常工厂Bean
     * 当Spring容器中不存在AttemptLimitExceptionFactory类型的Bean时，创建并注册DefaultAttemptLimitExceptionFactory实例
     * @return AttemptLimitExceptionFactory 默认的尝试限制异常工厂实例
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(AttemptLimitExceptionFactory::class)
    fun attemptLimitDefault(): AttemptLimitExceptionFactory =
        DefaultAttemptLimitExceptionFactory()

    /**
     * 创建默认的冷却期异常工厂Bean
     * 当Spring容器中不存在CooldownExceptionFactory类型的Bean时，创建并注册DefaultCooldownExceptionFactory实例
     * @return CooldownExceptionFactory 默认的冷却期异常工厂实例
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(CooldownExceptionFactory::class)
    fun cooldownDefault(): CooldownExceptionFactory =
        DefaultCooldownExceptionFactory()

    /**
     * 创建默认的幂等性异常工厂Bean
     * 当Spring容器中不存在IdempotentExceptionFactory类型的Bean时，创建并注册DefaultIdempotentExceptionFactory实例
     * @return IdempotentExceptionFactory 默认的幂等性异常工厂实例
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(IdempotentExceptionFactory::class)
    fun idempotentDefault(): IdempotentExceptionFactory =
        DefaultIdempotentExceptionFactory()

    /**
     * 创建默认的限流异常工厂Bean
     * 当Spring容器中不存在RateLimitExceptionFactory类型的Bean时，创建并注册DefaultRateLimitExceptionFactory实例
     * @return RateLimitExceptionFactory 默认的限流异常工厂实例
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(RateLimitExceptionFactory::class)
    fun rateLimitDefault(): RateLimitExceptionFactory =
        DefaultRateLimitExceptionFactory()

    /**
     * 创建限流异常工厂解析器的Bean
     *
     * @param applicationContext 应用上下文，用于获取Bean信息
     * @param defaultFactoryProvider 默认异常工厂提供者，用于获取默认的限流异常工厂实例
     * @return RateLimitExceptionFactoryResolver 限流异常工厂解析器实例
     */
    @Bean
    @ConditionalOnBean
    fun rateLimitExceptionFactoryResolver(
        applicationContext: ApplicationContext,
        defaultFactoryProvider: ObjectProvider<RateLimitExceptionFactory>,
    ): RateLimitExceptionFactoryResolver {
        return RateLimitExceptionFactoryResolver(
            applicationContext,
            defaultFactoryProvider
        )
    }

    /**
     * 创建幂等性异常工厂解析器的Bean
     *
     * @param applicationContext 应用上下文，用于获取Bean信息
     * @param defaultFactoryProvider 默认异常工厂提供者，用于获取默认的幂等性异常工厂实例
     * @return IdempotencyExceptionFactoryResolver 幂等性异常工厂解析器实例
     */
    @Bean
    @ConditionalOnBean
    fun idempotencyExceptionFactoryResolver(
        applicationContext: ApplicationContext,
        defaultFactoryProvider: ObjectProvider<IdempotentExceptionFactory>,
    ): IdempotentExceptionFactoryResolver {
        return IdempotentExceptionFactoryResolver(
            applicationContext,
            defaultFactoryProvider
        )
    }

    /**
     * 创建冷却期异常工厂解析器的Bean
     *
     * @param applicationContext 应用上下文，用于获取Bean信息
     * @param defaultFactoryProvider 默认异常工厂提供者，用于获取默认的冷却期异常工厂实例
     * @return CooldownExceptionFactoryResolver 冷却期异常工厂解析器实例
     */
    @Bean
    @ConditionalOnBean
    fun cooldownExceptionFactoryResolver(
        applicationContext: ApplicationContext,
        defaultFactoryProvider: ObjectProvider<CooldownExceptionFactory>,
    ): CooldownExceptionFactoryResolver {
        return CooldownExceptionFactoryResolver(
            applicationContext,
            defaultFactoryProvider
        )
    }

    /**
     * 创建尝试次数限制异常工厂解析器的Bean
     *
     * @param applicationContext 应用上下文，用于获取Bean信息
     * @param defaultFactoryProvider 默认异常工厂提供者，用于获取默认的尝试次数限制异常工厂实例
     * @return AttemptLimitExceptionFactoryResolver 尝试次数限制异常工厂解析器实例
     */
    @Bean
    @ConditionalOnBean
    fun attemptLimitExceptionFactoryResolver(
        applicationContext: ApplicationContext,
        defaultFactoryProvider: ObjectProvider<AttemptLimitExceptionFactory>,
    ): AttemptLimitExceptionFactoryResolver {
        return AttemptLimitExceptionFactoryResolver(
            applicationContext,
            defaultFactoryProvider
        )
    }

    /**
     * 创建幂等性返回值处理器的Bean
     *
     * @param codec 用于序列化和反序列化的编解码器
     * @return 幂等性返回值处理器实例
     */
    @Bean
    @ConditionalOnBean
    fun idempotentReturnAdvice(codec: PayloadCodec): IdempotencyReturnAdvice {
        return IdempotencyReturnAdvice(codec)
    }

    /**
     * 创建 Redis 键模板注册中心实例。
     *
     * @param redis Redis 操作模板
     * @param safeguardProperties 防护配置属性
     * @return RedisKeyTemplateRegistry 实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun redisKeyTemplateRegistry(
        redis: StringRedisTemplate,
        safeguardProperties: SafeguardProperties,
    ): KeyTemplateRegistry =
        RedisKeyTemplateRegistry(redis, safeguardProperties)

    /**
     * 创建键工厂实例。
     *
     * @param registry 键模板注册中心
     * @return KeyFactory 实例
     */
    @Bean("keyF")
    @ConditionalOnMissingBean(KeyFactory::class)
    fun keyFactory(registry: KeyTemplateRegistry) = KeyFactory(registry)

    /**
     * 提供默认的无操作指标收集器。
     *
     * @return NoopSafeguardMetrics 实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun noopSafeguardMetrics(): SafeguardMetrics = NoopSafeguardMetrics

    /**
     * 创建基于 Redisson 的代理管理器，用于支持分布式限流。
     *
     * @param redisson Redisson 客户端
     * @return ProxyManager<String> 实例
     */
    @Bean
    @ConditionalOnMissingBean(ProxyManager::class)
    fun proxyManager(redisson: RedissonClient): ProxyManager<String> {
        val commandExecutor = (redisson as Redisson).commandExecutor
        return Bucket4jRedisson.casBasedBuilder(
            commandExecutor
        ).keyMapper(
            Mapper.STRING
        ).apply {
            clientSideConfig.withExpirationAfterWriteStrategy(
                ExpirationAfterWriteStrategy
                    .basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(10))
            )
        }
            .build()
    }

    /**
     * 创建键解析支持类实例。
     *
     * @param beanFactory Spring Bean 工厂
     * @param keyFactory 键工厂
     * @return KeyResolutionSupport 实例
     */
    @Bean
    fun keyResolutionSupport(beanFactory: BeanFactory, keyFactory: KeyFactory) =
        KeyResolutionSupport(beanFactory, keyFactory)

    /**
     * 创建 Redis 键构建器实例。
     *
     * @return RedisKeyBuilder 实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun redisKeyBuilder() = RedisKeyBuilder("sg")

    /**
     * 创建 Lua 脚本执行器实例。
     *
     * @param redis Redis 操作模板
     * @return LuaScriptExecutor 实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun luaScriptExecutor(redis: StringRedisTemplate) = LuaScriptExecutor(redis)

    /**
     * 创建负载编解码器实例。
     *
     * @param mapper JSON 映射器
     * @return PayloadCodec 实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun payloadCodec(mapper: ObjectMapper): PayloadCodec = JacksonPayloadCodec(mapper)

    /**
     * 创建冷却守卫实例。
     *
     * @param redis Redis 操作模板
     * @param key Redis 键构建器
     * @return CooldownGuard 实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun cooldownGuard(redis: StringRedisTemplate, key: RedisKeyBuilder): CooldownGuard =
        RedisCooldownGuard(redis, key)

    /**
     * 创建幂等管理器实例。
     *
     * @param redis Redis 操作模板
     * @param key Redis 键构建器
     * @param lua Lua 脚本执行器
     * @return IdempotencyManager 实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun idempotencyManager(
        redis: StringRedisTemplate,
        key: RedisKeyBuilder,
        lua: LuaScriptExecutor,
    ): IdempotencyManager =
        RedisIdempotencyManager(redis, key, lua)

    /**
     * 创建限流器实例，根据配置选择使用 Lua 或 Bucket4j 实现。
     *
     * @param props 防护配置属性
     * @param lua Lua 脚本执行器
     * @param key Redis 键构建器
     * @param proxyManager 分布式代理管理器
     * @return RateLimiter 实例
     */
    @Bean
    @ConditionalOnMissingBean(RateLimiter::class)
    fun rateLimiter(
        props: SafeguardProperties,
        lua: LuaScriptExecutor,
        key: RedisKeyBuilder,
        proxyManager: ProxyManager<String>,
    ): RateLimiter =
        when (props.rateLimiterEngine.lowercase()) {
            "lua" -> LuaRedisRateLimiter(lua, key)
            "bucket4j" -> Bucket4jRateLimiter(proxyManager)
            else -> LuaRedisRateLimiter(lua, key)
        }

    /**
     * 创建尝试限制管理器实例，根据配置选择使用 Lua 或 Bucket4j 实现。
     *
     * @param props 防护配置属性
     * @param lua Lua 脚本执行器
     * @param key Redis 键构建器
     * @param proxyManager 分布式代理管理器
     * @return AttemptLimitManager 实例
     */
    @Bean
    @ConditionalOnMissingBean(AttemptLimitManager::class)
    fun attemptLimitManager(
        props: SafeguardProperties,
        lua: LuaScriptExecutor,
        key: RedisKeyBuilder,
        proxyManager: ProxyManager<String>,
        redisson: RedissonClient,
    ): AttemptLimitManager =
        when (props.attemptLimiterEngine.lowercase()) {
            "lua" -> LuaAttemptLimitManager(lua, key)
            "bucket4j" -> Bucket4jAttemptLimitManager(proxyManager, redisson, key)
            else -> LuaAttemptLimitManager(lua, key)
        }

    /**
     * 创建尝试限制切面实例。
     *
     * @param limiter 尝试限制管理器，用于控制尝试次数
     * @param metrics 指标收集器，用于记录尝试限制相关的监控数据
     * @param keyResolutionSupport 键解析支持类，用于生成唯一标识键
     * @param request HTTP 请求对象，用于获取请求上下文信息
     * @param resolver 异常工厂解析器，用于处理尝试限制异常
     * @return AttemptLimitAspect 实例，用于实现尝试限制逻辑的切面
     */
    @Bean
    @ConditionalOnMissingBean(AttemptLimitAspect::class)
    fun attemptLimitAspect(
        limiter: AttemptLimitManager,
        metrics: SafeguardMetrics,
        keyResolutionSupport: KeyResolutionSupport,
        request: HttpServletRequest,
        resolver: AttemptLimitExceptionFactoryResolver,
    ): AttemptLimitAspect {
        log.info("已启用尝试限制切面...")
        return AttemptLimitAspect(limiter, metrics, keyResolutionSupport, request, resolver)
    }

    /**
     * 创建冷却时间切面实例。
     *
     * @param guard 冷却守卫，用于判断是否处于冷却状态
     * @param beanFactory Spring Bean 工厂，用于获取其他 Spring 管理的 Bean
     * @param metrics 指标收集器，用于记录冷却时间相关的监控数据
     * @param keyResolutionSupport 键解析支持类，用于生成唯一标识键
     * @param resolver 异常工厂解析器，用于处理冷却时间异常
     * @return CooldownAspect 实例，用于实现冷却时间控制逻辑的切面
     */
    @Bean
    @ConditionalOnMissingBean(CooldownAspect::class)
    @ConditionalOnBean(CooldownGuard::class)
    fun cooldownAspect(
        guard: CooldownGuard,
        beanFactory: BeanFactory,
        metrics: SafeguardMetrics,
        keyResolutionSupport: KeyResolutionSupport,
        resolver: CooldownExceptionFactoryResolver,
    ): CooldownAspect {
        log.info("已启用冷却时间切面...")
        return CooldownAspect(guard, beanFactory, metrics, keyResolutionSupport, resolver)
    }

    /**
     * 创建幂等切面实例。
     *
     * @param idem 幂等管理器，用于处理幂等逻辑
     * @param codec 负载编解码器，用于序列化和反序列化请求负载
     * @param props 防护配置属性，提供幂等相关的配置参数
     * @param beanFactory Spring Bean 工厂，用于获取其他 Spring 管理的 Bean
     * @param metrics 指标收集器，用于记录幂等相关的监控数据
     * @param keyResolutionSupport 键解析支持类，用于生成唯一标识键
     * @param resolver 异常工厂解析器，用于处理幂等异常
     * @return IdempotentAspect 实例，用于实现幂等逻辑的切面
     */
    @Bean
    @ConditionalOnMissingBean(IdempotentAspect::class)
    fun idempotentAspect(
        idem: IdempotencyManager,
        codec: PayloadCodec,
        props: SafeguardProperties,
        beanFactory: BeanFactory,
        metrics: SafeguardMetrics,
        keyResolutionSupport: KeyResolutionSupport,
        resolver: IdempotentExceptionFactoryResolver,
    ): IdempotentAspect {
        log.info("已启用幂等切面...")
        return IdempotentAspect(idem, codec, props, beanFactory, metrics, keyResolutionSupport, resolver)
    }

    /**
     * 创建限流切面实例。
     *
     * @param limiter 限流器，用于控制请求频率
     * @param beanFactory Spring Bean 工厂，用于获取其他 Spring 管理的 Bean
     * @param metrics 指标收集器，用于记录限流相关的监控数据
     * @param keyResolutionSupport 键解析支持类，用于生成唯一标识键
     * @param resolver 异常工厂解析器，用于处理限流异常
     * @return RateLimitAspect 实例，用于实现限流逻辑的切面
     */
    @Bean
    @ConditionalOnMissingBean(RateLimitAspect::class)
    @ConditionalOnBean(RateLimiter::class)
    fun rateLimitAspect(
        limiter: RateLimiter,
        beanFactory: BeanFactory,
        metrics: SafeguardMetrics,
        keyResolutionSupport: KeyResolutionSupport,
        resolver: RateLimitExceptionFactoryResolver,
    ): RateLimitAspect {
        log.info("已启用限流切面...")
        return RateLimitAspect(limiter, beanFactory, metrics, keyResolutionSupport, resolver)
    }

}
