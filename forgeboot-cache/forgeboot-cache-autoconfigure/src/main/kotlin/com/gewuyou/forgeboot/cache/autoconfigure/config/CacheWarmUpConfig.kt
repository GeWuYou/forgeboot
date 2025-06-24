package com.gewuyou.forgeboot.cache.autoconfigure.config

import com.gewuyou.forgeboot.cache.api.service.CacheWarmUpService
import com.gewuyou.forgeboot.cache.impl.runner.CacheWarmUpRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 缓存预热配置类
 *
 * 该配置类用于定义与缓存预热相关的 Bean，确保应用启动时能够自动执行缓存预热逻辑。
 *
 * @since 2025-06-21 11:41:34
 * @author gewuyou
 */
@Configuration
class CacheWarmUpConfig {

    /**
     * 创建 CacheWarmUpRunner Bean
     *
     * 该方法将一组 CacheWarmUpService 实例注入到 CacheWarmUpRunner 中，
     * 用于在应用启动时运行缓存预热任务。
     *
     * @param services 缓存预热服务的列表，每个服务实现具体的预热逻辑
     * @return 返回一个 CacheWarmUpRunner 实例，用于启动缓存预热流程
     */
    @Bean
    fun cacheWarmUpRunner(
        services: List<CacheWarmUpService>
    ): CacheWarmUpRunner {
        return CacheWarmUpRunner(services)
    }
}