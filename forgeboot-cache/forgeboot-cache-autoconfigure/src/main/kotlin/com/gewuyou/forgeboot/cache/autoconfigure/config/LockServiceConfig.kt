package com.gewuyou.forgeboot.cache.autoconfigure.config

import com.gewuyou.forgeboot.cache.api.service.LockService
import com.gewuyou.forgeboot.cache.impl.service.RedisLockService
import org.redisson.api.RedissonClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 锁服务配置类
 *
 * 该配置类用于定义与锁服务相关的 Bean，确保应用中可以正常使用分布式锁功能。
 *
 * @since 2025-06-21 11:42:20
 * @author gewuyou
 */
@Configuration
class LockServiceConfig {

    /**
     * 创建 LockService 锁服务 Bean
     *
     * 该方法定义了一个 LockService 类型的 Bean，用于提供基于 Redisson 的分布式锁实现。
     * 如果 Spring 容器中尚未存在 LockService 实例，则会通过此方法创建一个。
     *
     * @param redissonClient Redisson 客户端实例，用于与 Redis 进行交互并创建锁
     * @return 返回一个 LockService 实例，具体实现为 RedisLockService
     */
    @Bean
    @ConditionalOnMissingBean
    fun lockService(redissonClient: RedissonClient): LockService {
        return RedisLockService(redissonClient)
    }
}
