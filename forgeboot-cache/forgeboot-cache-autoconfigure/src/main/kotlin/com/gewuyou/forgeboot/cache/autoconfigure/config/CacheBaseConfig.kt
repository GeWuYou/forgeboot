package com.gewuyou.forgeboot.cache.autoconfigure.config

import com.gewuyou.forgeboot.cache.api.generator.KeyGenerator
import com.gewuyou.forgeboot.cache.impl.generator.DefaultKeyGenerator
import com.gewuyou.forgeboot.cache.impl.utils.RedisKeyScanner
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate

/**
 * 缓存基础配置类，用于定义缓存相关的基础 Bean。
 *
 * @since 2025-06-20 23:31:09
 * @author gewuyou
 */
@Configuration(proxyBeanMethods = false)
class CacheBaseConfig {
    /**
     * 创建 StringRedisTemplate Bean。
     *
     * StringRedisTemplate 是 Spring Data Redis 提供的模板类，
     * 专门用于操作 Redis 中的字符串类型数据。
     *
     * @return 配置好的 StringRedisTemplate 实例
     */
    @Bean
    fun redisTemplate(): StringRedisTemplate {
        return StringRedisTemplate()
    }

    /**
     * 创建 RedisConnectionFactory Bean。
     *
     * RedisConnectionFactory 是连接 Redis 数据库的基础组件，
     * 使用 Lettuce 客户端实现连接。
     *
     * @return 配置好的 LettuceConnectionFactory 实例
     */
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory()
    }

    /**
     * 创建 RedisKeyScanner Bean。
     *
     * RedisKeyScanner 用于扫描 Redis 中的键（Key），便于管理与维护缓存数据。
     *
     * @param redisConnectionFactory 已注入的 Redis 连接工厂实例
     * @return 配置好的 RedisKeyScanner 实例
     */
    @Bean
    fun redisKeyScanner(redisConnectionFactory: RedisConnectionFactory): RedisKeyScanner {
        return RedisKeyScanner(redisConnectionFactory)
    }

    /**
     * 创建默认的 KeyGenerator Bean。
     *
     * DefaultKeyGenerator 用于生成统一格式的缓存键，确保缓存键的命名一致性。
     * 若上下文中不存在 KeyGenerator 类型的 Bean，则自动创建此默认实例。
     *
     * @return 默认的 KeyGenerator 实现实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun keyGenerator(): KeyGenerator {
        return DefaultKeyGenerator()
    }
}