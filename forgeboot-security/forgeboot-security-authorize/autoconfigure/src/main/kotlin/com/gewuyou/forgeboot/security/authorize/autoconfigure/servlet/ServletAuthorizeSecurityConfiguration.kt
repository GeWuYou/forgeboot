package com.gewuyou.forgeboot.security.authorize.autoconfigure.servlet

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.security.authorize.api.core.config.SecurityAuthorizeProperties
import com.gewuyou.forgeboot.security.authorize.api.core.manager.AccessManager
import com.gewuyou.forgeboot.security.authorize.api.core.resolver.PermissionResolver
import com.gewuyou.forgeboot.security.authorize.impl.servlet.adapter.DynamicAuthorizationManagerAdapter
import com.gewuyou.forgeboot.security.authorize.impl.servlet.handler.AuthorizationExceptionHandler
import com.gewuyou.forgeboot.security.authorize.impl.servlet.registrar.StatelessSecurityFilterChainRegistrar
import com.gewuyou.forgeboot.security.core.common.constants.SecurityConstants
import com.gewuyou.forgeboot.security.core.common.customizer.HttpSecurityCustomizer
import com.gewuyou.forgeboot.security.core.common.registrar.SecurityFilterChainRegistrar
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.security.web.util.matcher.AnyRequestMatcher

/**
 *servlet authorize 安全配置
 *
 * @since 2025-06-24 16:54:19
 * @author gewuyou
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
class ServletAuthorizeSecurityConfiguration(
    private val accessManager: AccessManager,
    private val permissionResolver: PermissionResolver,
) {
    /**
     * 创建并返回一个认证管理器。
     *
     * 初始化一个基于提供的认证提供者的认证管理器，用于支持多种认证方式。
     *
     * @param authenticationProviders 所有可用的认证提供者列表
     * @return 返回初始化好的 AuthenticationManager 实例，具体实现为 ProviderManager
     */
    @Bean
    @Primary
    @ConditionalOnBean
    fun authenticationManager(
        authenticationProviders: List<AuthenticationProvider>,
    ): AuthenticationManager = ProviderManager(authenticationProviders)

    /**
     * 创建默认的安全过滤链，适用于 Servlet 编程模型
     *
     * @param registrar 安全过滤链注册器
     * @param http HttpSecurity 配置对象
     * @return 构建后的安全过滤链实例
     * @throws Exception 构建过程中可能抛出的异常
     */
    @Bean(name = ["defaultSecurityFilterChain"])
    @ConditionalOnBean
    @Throws(Exception::class)
    fun defaultSecurityFilterChain(
        registrar: SecurityFilterChainRegistrar,
        http: HttpSecurity,
    ): SecurityFilterChain = registrar.buildChain(
        SecurityConstants.DEFAULT_CHAIN_ID,
        http,
        AnyRequestMatcher.INSTANCE
    ) { config ->
        config.authorizeHttpRequests {
            it.anyRequest().denyAll()
        }
    }

    /**
     * 注册授权异常处理器
     *
     * @param objectMapper 用于序列化错误响应的对象映射器
     * @param securityAuthorizeProperties 授权配置属性
     * @return AccessDeniedHandler 实例
     */
    @Bean
    @ConditionalOnClass(AccessDeniedHandler::class)
    @ConditionalOnBean
    fun authorizationExceptionHandler(
        objectMapper: ObjectMapper,
        securityAuthorizeProperties: SecurityAuthorizeProperties,
    ): AccessDeniedHandler {
        return AuthorizationExceptionHandler(objectMapper, securityAuthorizeProperties)
    }

    /**
     * 动态授权管理器适配器
     *
     * @return 适配后的授权管理器实例
     */
    @Bean
    @ConditionalOnBean
    fun dynamicAuthorizationManager(): AuthorizationManager<RequestAuthorizationContext> {
        return DynamicAuthorizationManagerAdapter(accessManager, permissionResolver)
    }

    /**
     * 无状态安全过滤链注册器
     *
     * @param authorizationManager 授权管理器
     * @param accessDeniedHandler 授权失败处理器
     * @param httpSecurityCustomizer HTTP 安全配置定制器列表
     * @return 安全过滤链注册器实例
     */
    @Bean
    @ConditionalOnBean
    fun statelessSecurityWebFilterChainRegistrar(
        authorizationManager: AuthorizationManager<RequestAuthorizationContext>,
        accessDeniedHandler: AccessDeniedHandler,
        httpSecurityCustomizer: List<HttpSecurityCustomizer>,
    ): SecurityFilterChainRegistrar {
        return StatelessSecurityFilterChainRegistrar(
            httpSecurityCustomizer,
            authorizationManager,
            accessDeniedHandler
        )
    }
}