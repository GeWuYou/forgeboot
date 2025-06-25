package com.gewuyou.forgeboot.security.authorize.impl.servlet.customizer

import com.gewuyou.forgeboot.security.core.common.constants.SecurityConstants
import com.gewuyou.forgeboot.security.core.common.customizer.HttpSecurityCustomizer
import jakarta.servlet.Filter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * API 密钥 HTTP 安全定制器
 *
 * 用于为基于 API 密钥的身份验证机制定制 Spring Security 的 HTTP 安全配置。
 * 该类实现了 HttpSecurityCustomizer 接口，能够根据指定的安全链 ID 决定是否启用当前的认证逻辑，
 * 并负责将 API 密钥身份验证过滤器和提供者注入到安全配置中。
 *
 * @property apiKeyAuthenticationProvider 提供 API 密钥身份验证逻辑的认证提供者
 * @property apiKeyAuthenticationFilter     处理 API 密钥身份验证请求的过滤器实例
 * @since 2025-06-25 16:09:38
 * @author gewuyou
 */
class ApiKeyHttpSecurityCustomizer(
    private val apiKeyAuthenticationFilter: Filter
) : HttpSecurityCustomizer {

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
     * 执行安全配置的定制逻辑
     *
     * 将 API 密钥身份验证相关的组件注册到 Spring Security 流程中。
     * 包括：
     * - 注册认证提供者（apiKeyAuthenticationProvider）
     * - 在请求处理流程中插入 ApiKeyAuthenticationFilter 过滤器
     *   该过滤器会在 UsernamePasswordAuthenticationFilter 前执行
     *
     * @param http 用于构建 HTTP 安全策略的 HttpSecurity 实例
     *             通过此参数可添加或修改安全规则，如认证、授权等
     */
    override fun customize(http: HttpSecurity) {
        // 配置安全逻辑：注册认证提供者并将 API 密钥过滤器插入到过滤器链中的合适位置
        http
            .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}