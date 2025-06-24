package com.gewuyou.forgeboot.cache.autoconfigure.config

import com.gewuyou.forgeboot.cache.api.config.CacheProperties
import com.gewuyou.forgeboot.cache.api.policy.CachePolicy
import com.gewuyou.forgeboot.cache.api.policy.NullValuePolicy
import com.gewuyou.forgeboot.cache.impl.policy.AllowNullCachePolicy
import com.gewuyou.forgeboot.cache.impl.policy.NullValueCachePolicy
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 缓存策略配置类
 *
 * 该配置类用于定义缓存相关的策略 Bean，当前主要包含 NullValuePolicy 的默认实现。
 * 所有需要注册为 Spring Bean 的缓存策略应在本类中进行声明和配置。
 *
 * @since 2025-06-21 11:37:50
 * @author gewuyou
 */
@Configuration
class CachePolicyConfig {
    /**
     * 创建 NullValuePolicy 类型的 Bean，用于控制是否允许缓存 null 值。
     *
     * 当前使用 AllowNullCachePolicy 作为默认实现，表示允许缓存 null 值。
     * 如果应用上下文中已存在同类型的 Bean，则跳过此 Bean 的创建。
     *
     * @return 返回一个 NullValuePolicy 接口的实现对象
     */
    @Bean
    @ConditionalOnMissingBean
    fun nullCachePolicy(cacheProperties: CacheProperties): NullValuePolicy {
        return AllowNullCachePolicy(cacheProperties)
    }

    /**
     * 创建 CachePolicy 类型的 Bean，用于包装基础的 NullValuePolicy 实现。
     *
     * 该方法将 NullValuePolicy 实例封装为一个 CachePolicy 接口的实现对象，
     * 允许在缓存操作中应用 null 值策略。如果应用上下文中已存在同类型的 Bean，
     * 则跳过此 Bean 的创建。
     *
     * @param nullValuePolicy 注入的 NullValuePolicy 实例，用于决定是否允许缓存 null 值
     * @return 返回封装后的 CachePolicy 对象
     */
    @Bean("nullValueCachePolicy")
    @ConditionalOnMissingBean
    fun nullValueCachePolicy(nullValuePolicy: NullValuePolicy): CachePolicy {
        return NullValueCachePolicy(nullValuePolicy)
    }

}