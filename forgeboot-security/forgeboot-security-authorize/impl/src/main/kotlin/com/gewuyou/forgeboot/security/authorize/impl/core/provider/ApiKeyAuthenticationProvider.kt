package com.gewuyou.forgeboot.security.authorize.impl.core.provider

import com.gewuyou.forgeboot.security.authorize.api.core.service.ApiKeyService
import com.gewuyou.forgeboot.security.core.common.token.ApiKeyAuthenticationToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication

/**
 * API 密钥身份验证提供程序
 *
 * 该类实现 Spring Security 的 AuthenticationProvider 接口，用于处理基于 API Key 的身份验证流程。
 *
 * @property apiKeyService 用于校验 API Key 并获取用户信息和权限的服务组件
 * @constructor 创建一个 ApiKeyAuthenticationProvider 实例
 *
 * @since 2025-06-25 13:09:43
 * @author gewuyou
 */
class ApiKeyAuthenticationProvider(
    private val apiKeyService: ApiKeyService
) : AuthenticationProvider {

    /**
     * 执行身份验证操作
     *
     * 将传入的 Authentication 对象转换为 ApiKeyAuthenticationToken，
     * 然后使用 apiKeyService 校验 API Key 并获取相关用户信息和权限。
     *
     * @param authentication 包含 API Key 的身份验证请求对象
     * @return 返回已认证的 Authentication 对象
     */
    override fun authenticate(authentication: Authentication): Authentication {
        val token = authentication as ApiKeyAuthenticationToken
        val keyInfo = apiKeyService.validate(token.apiKey)

        return ApiKeyAuthenticationToken(
            token.apiKey,
            keyInfo.principal,
            keyInfo.authorities
        ).apply {
            isAuthenticated = true
        }
    }

    /**
     * 判断此 Provider 是否支持给定的身份验证类型
     *
     * 用于确定当前 Provider 是否可以处理指定的 Authentication 类型。
     * 此方法被调用时会检查是否为 ApiKeyAuthenticationToken 或其子类。
     *
     * @param authentication 要检查的身份验证类
     * @return 如果支持则返回 true，否则返回 false
     */
    override fun supports(authentication: Class<*>): Boolean {
        return ApiKeyAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}
