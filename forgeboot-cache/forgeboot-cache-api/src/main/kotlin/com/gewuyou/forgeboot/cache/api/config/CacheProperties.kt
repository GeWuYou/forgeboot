package com.gewuyou.forgeboot.cache.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * 缓存属性配置类
 *
 * 该类用于定义缓存相关的配置属性，通过@ConfigurationProperties绑定配置前缀"forgeboot.cache"
 * 支持配置默认缓存过期时间（TTL）
 *
 * @since 2025-06-17 10:04:48
 * @author gewuyou
 */
@ConfigurationProperties("forgeboot.cache")
class CacheProperties {
    /**
     * 默认缓存过期时间（Time To Live）
     *
     * 用于指定缓存项在未被访问后的最大存活时间
     * 默认值为15分钟
     */
    var theDefaultCacheTTL: Duration = Duration.ofMinutes(15)
    /**
     * 缓存项为null时使用的占位符
     *
     * 用于在缓存项为null时返回的占位符
     * 默认值为"__NULL__"
     */
    var nullValuePlaceholder: String = "__NULL__"
}