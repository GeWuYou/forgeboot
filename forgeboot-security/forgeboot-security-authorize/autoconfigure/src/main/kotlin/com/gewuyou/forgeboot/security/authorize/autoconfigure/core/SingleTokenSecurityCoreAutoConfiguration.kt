package com.gewuyou.forgeboot.security.authorize.autoconfigure.core

import com.gewuyou.forgeboot.security.authorize.api.core.validator.SingleTokenValidator
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
     * 提供一个默认的 SingleTokenValidator Bean，用于验证单点登录令牌。
     *
     * 如果上下文中尚未定义此类 Bean，则使用此默认实现。
     * 默认实现会在调用 validate 方法时抛出 UnsupportedOperationException，
     * 提示使用者应提供自定义的 SingleTokenValidator 实现。
     *
     * @return 返回一个 SingleTokenValidator 接口的默认实现
     */
    @Bean
    @ConditionalOnMissingBean
    fun singleTokenValidator(): SingleTokenValidator<SingleTokenPrincipal> {
        return object : SingleTokenValidator<SingleTokenPrincipal> {
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
     * 该方法创建并返回一个 SingleTokenAuthenticationProvider 实例，
     * 用于在 Spring Security 框架中处理基于单点令牌的认证逻辑。
     * 如果上下文中尚未定义同名 Bean，则进行注册。
     *
     * @param singleTokenValidator 提供的 SingleTokenValidator 实例，
     *                             用于执行具体的令牌验证逻辑
     * @return 返回配置好的 AuthenticationProvider 实现类实例
     */
    @Bean("singleTokenAuthenticationProvider")
    @ConditionalOnMissingBean
    fun singleTokenAuthenticationProvider(singleTokenValidator: SingleTokenValidator<SingleTokenPrincipal>): AuthenticationProvider {
        return SingleTokenAuthenticationProvider(singleTokenValidator)
    }
}