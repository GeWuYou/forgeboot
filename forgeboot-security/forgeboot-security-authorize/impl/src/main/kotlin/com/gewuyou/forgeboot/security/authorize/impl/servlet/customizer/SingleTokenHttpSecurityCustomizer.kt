package com.gewuyou.forgeboot.security.authorize.impl.servlet.customizer

import com.gewuyou.forgeboot.security.core.common.constants.SecurityConstants
import com.gewuyou.forgeboot.security.core.common.customizer.HttpSecurityCustomizer
import jakarta.servlet.Filter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * 基于单 Token 认证的安全配置定制器
 *
 * 该类用于在 Spring Security 的过滤器链中注册并配置 API 密钥身份验证逻辑，
 * 仅当当前安全链标识符匹配 SINGLE_TOKEN_CHAIN_ID 时生效。
 *
 * @property singleTokenAuthenticationFilter 处理 API 密钥身份验证请求的过滤器实例
 * @since 2025-06-25 16:09:38
 * @author gewuyou
 */
class SingleTokenHttpSecurityCustomizer(
    private val singleTokenAuthenticationFilter: Filter
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
        return SecurityConstants.SINGLE_TOKEN_CHAIN_ID == chainId
    }

    /**
     * 执行安全配置的定制逻辑
     *
     * 将 API 密钥身份验证过滤器添加到 Spring Security 的过滤器链中，
     * 置于 UsernamePasswordAuthenticationFilter 之前以确保优先处理 Token 请求。
     *
     * @param http 用于构建 HTTP 安全策略的 HttpSecurity 实例
     *             通过此参数可添加或修改安全规则，如认证、授权等
     */
    override fun customize(http: HttpSecurity) {
        // 配置安全逻辑：注册认证提供者并将 API 密钥过滤器插入到过滤器链中的合适位置
        http
            .addFilterBefore(singleTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}