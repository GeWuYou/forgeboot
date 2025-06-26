package com.gewuyou.forgeboot.security.authorize.impl.webflux.customizer

import com.gewuyou.forgeboot.security.core.common.constants.SecurityConstants
import com.gewuyou.forgeboot.security.core.common.customizer.ServerHttpSecurityCustomizer
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.web.server.WebFilter

/**
 * 用于在 Spring WebFlux 环境下进行基于 Token 的认证安全配置定制。
 *
 * 此类实现 ServerHttpSecurityCustomizer 接口，负责将自定义的 Token 认证逻辑集成到安全链中。
 * 它通过 supports 方法判断是否适用于当前的安全链配置，并通过 customize 方法插入过滤器。
 *
 * @constructor
 * @param reactiveSingleTokenAuthenticationFilter 自定义的 WebFilter 实现，用于执行 Token 认证逻辑
 */
class SingleTokenServerHttpSecurityCustomizer(
    private val reactiveSingleTokenAuthenticationFilter: WebFilter,
) : ServerHttpSecurityCustomizer {
    /**
     * 判断当前定制器是否支持处理指定的安全链配置。
     *
     * 该方法用于标识此定制器是否适用于给定 chainId 所代表的安全配置场景。
     * 在本实现中，仅当 chainId 与预定义的 API_KEY_CHAIN_ID 匹配时返回 true。
     *
     * @param chainId 安全链的唯一标识符，用于区分不同的安全配置场景
     * @return Boolean 返回 true 表示支持该 chainId，否则不支持
     */
    override fun supports(chainId: String): Boolean {
        return SecurityConstants.API_KEY_CHAIN_ID == chainId
    }

    /**
     * 自定义 ServerHttpSecurity 配置的方法。
     *
     * 此方法由框架调用，允许开发者插入特定于该安全链的配置逻辑。
     * 参数提供了 ServerHttpSecurity 实例，可以通过其添加、修改或删除安全相关的组件。
     *
     * @param http ServerHttpSecurity 实例，用于构建 WebFlux 安全配置
     */
    override fun customize(http: ServerHttpSecurity) {
        // 将 Token 认证过滤器添加到 WebFlux 安全链中的认证位置
        http.addFilterAt(reactiveSingleTokenAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
    }
}