package com.gewuyou.forgeboot.security.authorize.autoconfigure.core

import com.gewuyou.forgeboot.security.authorize.api.core.service.ApiKeyService
import com.gewuyou.forgeboot.security.authorize.impl.core.provider.ApiKeyAuthenticationProvider
import com.gewuyou.forgeboot.security.core.authorize.entities.ApiKeyPrincipal
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider

/**
 * API 密钥安全核心自动配置类。
 *
 * 该配置类用于注册与 API 密钥相关的 Bean，确保在 Spring 容器中存在必要的服务和认证提供者。
 * 主要作用包括：
 * - 提供默认的 ApiKeyService Bean（未实现时）。
 * - 提供 ApiKeyAuthenticationProvider Bean 以支持基于 API 密钥的身份验证。
 *
 * @since 2025-06-25 15:49:50
 * @author gewuyou
 */
@Configuration(proxyBeanMethods = false)
class ApiKeySecurityCoreAutoConfiguration {

    /**
     * 提供一个默认的 ApiKeyService Bean 实现。
     *
     * 如果容器中不存在 ApiKeyService 的实现，则会使用此默认 Bean。
     * 默认实现的 validate 方法始终抛出 UnsupportedOperationException，
     * 提示用户需要自定义并注册自己的 ApiKeyService 实现。
     *
     * @return 返回一个未实现的 ApiKeyService 对象
     */
    @Bean
    @ConditionalOnMissingBean
    fun apiKeyService(): ApiKeyService {
        return object : ApiKeyService {
            override fun validate(apiKey: String): ApiKeyPrincipal {
                throw UnsupportedOperationException("请提供 ApiKeyService 实现")
            }
        }
    }

    /**
     * 提供一个用于认证的 ApiKeyAuthenticationProvider Bean。
     *
     * 如果容器中尚不存在同名 Bean，则创建并返回 ApiKeyAuthenticationProvider 实例。
     * 该 Provider 使用传入的 apiKeyService 来处理具体的 API 密钥验证逻辑。
     *
     * @param apiKeyService 用于处理 API 密钥逻辑的服务实现
     * @return AuthenticationProvider 的具体实现对象
     */
    @Bean("apiKeyAuthenticationProvider")
    @ConditionalOnMissingBean
    fun apiKeyAuthenticationProvider(apiKeyService: ApiKeyService): AuthenticationProvider {
        return ApiKeyAuthenticationProvider(apiKeyService)
    }
}