package com.gewuyou.forgeboot.cache.impl.runner

import com.gewuyou.forgeboot.cache.api.service.CacheWarmUpService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner

/**
 * 缓存预热运行程序
 *
 * 该类实现 ApplicationRunner 接口，用于在应用启动完成后执行缓存预热操作。
 * 构造函数注入了多个 CacheWarmUpService 实现，每个服务对应一个需要预热的缓存资源。
 *
 * @property services 需要执行预热操作的所有缓存服务集合
 *
 * @since 2025-06-18 12:36:40
 * @author gewuyou
 */
class CacheWarmUpRunner(
    private val services: List<CacheWarmUpService>
) : ApplicationRunner {
    /**
     * 应用启动完成后执行的方法
     *
     * 遍历所有注入的 CacheWarmUpService 并调用 warmUp 方法执行缓存预热逻辑。
     * 该方法会在 Spring Boot 应用完成启动后自动触发。
     *
     * @param args 应用启动参数（可为空）
     */
    override fun run(args: ApplicationArguments) {
        services.forEach { it.warmUp() }
    }
}