package com.gewuyou.forgeboot.security.authorize.impl.webflux.customizer

import com.gewuyou.forgeboot.security.core.common.constants.SecurityConstants
import com.gewuyou.forgeboot.security.core.common.customizer.ServerHttpSecurityCustomizer
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.web.server.WebFilter

/**
 * API 密钥服务器 HTTP 安全定制器
 *
 * 该类用于在 Spring WebFlux 环境下基于 API 密钥进行认证的安全配置定制。
 * 它实现了 ServerHttpSecurityCustomizer 接口，能将自定义的认证逻辑集成到安全链中。
 *
 * @constructor
 * @param apiKeyReactiveAuthenticationFilter 自定义的 WebFilter 实现，用于执行 API 密钥认证逻辑
 */
class ApiKeyServerHttpSecurityCustomizer(
    private val apiKeyReactiveAuthenticationFilter: WebFilter,
) : ServerHttpSecurityCustomizer {
    /**
     * 判断当前定制器是否支持处理指定的安全链配置
     *
     * 此方法用于标识该定制器是否适用于特定的安全链配置。
     * 实现类应根据 chainId 参数决定是否启用此定制器的逻辑。
     *
     * @param chainId 安全链的唯一标识符，用于区分不同的安全配置场景
     * @return Boolean 返回 true 表示支持该 chainId，否则不支持
     */
    override fun supports(chainId: String): Boolean {
        return SecurityConstants.API_KEY_CHAIN_ID == chainId
    }

    /**
     * 自定义 ServerHttpSecurity 配置的方法
     *
     * 此方法由框架调用，允许开发者插入自定义的安全配置逻辑。
     * 方法参数提供了 ServerHttpSecurity 实例，可用于链式配置。
     *
     * @param http ServerHttpSecurity 实例，用于构建 WebFlux 安全配置
     */
    override fun customize(http: ServerHttpSecurity) {
        // 将过滤器添加到安全链中的认证位置
        http.addFilterAt(apiKeyReactiveAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
    }
}