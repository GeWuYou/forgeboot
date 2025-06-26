package com.gewuyou.forgeboot.security.authorize.autoconfigure.core

import com.gewuyou.forgeboot.security.authorize.api.core.service.SingleTokenService
import com.gewuyou.forgeboot.security.authorize.impl.core.provider.SingleTokenAuthenticationProvider
import com.gewuyou.forgeboot.security.core.authorize.entities.SingleTokenPrincipal
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider

/**
 * 配置类，用于自动配置单点令牌（Single Token）认证相关的核心组件。
 *
 * @since 2025-06-25 15:49:50
 * @author gewuyou
 */
@Configuration(proxyBeanMethods = false)
class SingleTokenSecurityCoreAutoConfiguration {

    /**
     * 提供一个默认的 SingleTokenService Bean，用于验证令牌并返回用户主体信息。
     *
     * 如果上下文中尚未定义此类 Bean，则使用此默认实现。
     * 默认实现会在调用 validate 方法时抛出 UnsupportedOperationException，
     * 提示使用者应提供自定义的 SingleTokenService 实现。
     *
     * @return 返回一个 SingleTokenService 接口的默认实现
     */
    @Bean
    @ConditionalOnMissingBean
    fun singleTokenService(): SingleTokenService {
        return object : SingleTokenService {
            /**
             * 验证给定的 token 并返回对应的用户主体信息。
             *
             * @param token 要验证的令牌字符串
             * @return 返回包含用户信息的 SingleTokenPrincipal 对象
             * @throws UnsupportedOperationException 始终抛出异常，提示需要自定义实现
             */
            override fun validate(token: String): SingleTokenPrincipal {
                throw UnsupportedOperationException("请提供 SingleTokenService 实现")
            }
        }
    }

    /**
     * 注册 SingleTokenAuthenticationProvider Bean，用于 Spring Security 的认证流程。
     *
     * 该认证提供者依赖于 SingleTokenService 来完成实际的令牌验证工作。
     * 如果上下文中尚未定义同名 Bean，则注册该 Bean。
     *
     * @param singleTokenService 用于令牌验证的服务实例
     * @return 返回配置好的 SingleTokenAuthenticationProvider 实例
     */
    @Bean("singleTokenAuthenticationProvider")
    @ConditionalOnMissingBean
    fun singleTokenAuthenticationProvider(singleTokenService: SingleTokenService): AuthenticationProvider {
        return SingleTokenAuthenticationProvider(singleTokenService)
    }
}