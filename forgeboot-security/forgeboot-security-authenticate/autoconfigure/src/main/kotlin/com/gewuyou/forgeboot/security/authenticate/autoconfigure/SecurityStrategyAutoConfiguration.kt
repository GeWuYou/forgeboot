package com.gewuyou.forgeboot.security.authenticate.autoconfigure

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.security.authenticate.api.config.SecurityAuthenticateProperties
import com.gewuyou.forgeboot.security.authenticate.api.strategy.AuthenticationFailureStrategy
import com.gewuyou.forgeboot.security.authenticate.api.strategy.AuthenticationSuccessStrategy
import com.gewuyou.forgeboot.security.authenticate.api.strategy.LoginRequestConverterStrategy
import com.gewuyou.forgeboot.security.authenticate.api.strategy.LogoutSuccessStrategy
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.impl.DefaultAuthenticationFailureStrategy
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.impl.DefaultAuthenticationSuccessStrategy
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.impl.DefaultLogoutSuccessStrategy
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.impl.UsernamePasswordLoginRequestConverterStrategy
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 安全策略自动配置类
 *
 * 该类定义了多个Bean用于注册认证相关的策略实现，确保系统中存在默认的安全策略处理逻辑。
 * 所有Bean都使用@ConditionalOnMissingBean注解，表示只有在没有手动定义相应Bean时才会创建默认实例。
 *
 * @since 2025-06-27 08:02:26
 * @author gewuyou
 */
@Configuration(proxyBeanMethods = false)
class SecurityStrategyAutoConfiguration {

    /**
     * 创建默认的认证成功策略Bean
     *
     * 如果上下文中不存在AuthenticationSuccessStrategy类型的Bean，则注册一个默认的实现。
     * 依赖于ObjectMapper用于序列化/反序列化操作。
     *
     * @param objectMapper Jackson的ObjectMapper实例，用于处理JSON数据
     * @return 默认的认证成功策略实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun defaultAuthenticationSuccessStrategy(objectMapper: ObjectMapper): AuthenticationSuccessStrategy {
        return DefaultAuthenticationSuccessStrategy(objectMapper)
    }

    /**
     * 创建默认的认证失败策略Bean
     *
     * 如果上下文中不存在AuthenticationFailureStrategy类型的Bean，则注册一个默认的实现。
     * 依赖于ObjectMapper和SecurityAuthenticateProperties，用于定制失败响应和配置参数。
     *
     * @param objectMapper   Jackson的ObjectMapper实例，用于处理JSON数据
     * @param properties     安全认证相关配置属性，用于定制行为
     * @return 默认的认证失败策略实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun defaultAuthenticationFailureStrategy(
        objectMapper: ObjectMapper,
        properties: SecurityAuthenticateProperties
    ): AuthenticationFailureStrategy {
        return DefaultAuthenticationFailureStrategy(objectMapper, properties)
    }

    /**
     * 创建默认的登出成功策略Bean
     *
     * 如果上下文中不存在LogoutSuccessStrategy类型的Bean，则注册一个默认的实现。
     * 依赖于ObjectMapper用于序列化/反序列化操作。
     *
     * @param objectMapper Jackson的ObjectMapper实例，用于处理JSON数据
     * @return 默认的登出成功策略实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun defaultLogoutSuccessStrategy(objectMapper: ObjectMapper): LogoutSuccessStrategy {
        return DefaultLogoutSuccessStrategy(objectMapper)
    }

    /**
     * 创建默认的登录请求转换策略Bean
     *
     * 如果上下文中不存在LoginRequestConverterStrategy类型的Bean，则注册一个基于用户名密码的默认实现。
     * 该Bean指定了名称"usernamePasswordLoginRequestConverterStrategy"，以便通过名称引用。
     *
     * @return 默认的登录请求转换策略实例
     */
    @Bean("usernamePasswordLoginRequestConverterStrategy")
    @ConditionalOnMissingBean
    fun defaultLoginRequestConverterStrategy(): LoginRequestConverterStrategy {
        return UsernamePasswordLoginRequestConverterStrategy()
    }
}