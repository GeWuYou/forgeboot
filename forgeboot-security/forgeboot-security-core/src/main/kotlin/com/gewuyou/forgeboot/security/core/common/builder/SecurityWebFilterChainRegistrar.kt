package com.gewuyou.forgeboot.security.core.common.builder

import com.gewuyou.forgeboot.security.core.common.wrapper.SecurityWebFilterChainWrapper
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher

/**
 * 安全 Web 过滤器链注册商接口
 *
 * 用于构建和注册自定义的安全过滤器链，适用于响应式 Spring Security 配置。
 *
 * @since 2025-06-24 12:35:00
 * @author gewuyou
 */
fun interface SecurityWebFilterChainRegistrar {
    /**
     * 构建安全过滤器链
     *
     * 此方法用于基于提供的匹配器和配置逻辑创建一个定制化的安全过滤器链。
     *
     * @param chainId 唯一标识此过滤器链的字符串，用于后续引用或管理
     * @param http 提供基础配置的 ServerHttpSecurity 对象，用于构建安全策略
     * @param matcher 用于匹配请求的 ServerWebExchangeMatcher 对象
     * @param config 用于配置安全策略的函数，接受 ServerHttpSecurity 对象作为参数
     * @return 返回构建完成的 ForgeBootSecurityWebFilterChain 实例
     */
    fun buildChain(
        chainId: String,
        http: ServerHttpSecurity,
        matcher: ServerWebExchangeMatcher,
        config: (ServerHttpSecurity) -> Unit
    ): SecurityWebFilterChainWrapper
}