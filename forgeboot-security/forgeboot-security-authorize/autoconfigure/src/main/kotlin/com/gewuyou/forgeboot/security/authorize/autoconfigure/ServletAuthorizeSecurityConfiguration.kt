package com.gewuyou.forgeboot.security.authorize.autoconfigure

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.security.authorize.api.config.SecurityAuthorizeProperties
import com.gewuyou.forgeboot.security.authorize.api.manager.AccessManager
import com.gewuyou.forgeboot.security.authorize.api.resolver.PermissionResolver
import com.gewuyou.forgeboot.security.authorize.impl.adapter.DynamicAuthorizationManagerAdapter
import com.gewuyou.forgeboot.security.authorize.impl.builder.StatelessSecurityFilterChainRegistrar
import com.gewuyou.forgeboot.security.authorize.impl.handler.AuthorizationExceptionHandler
import com.gewuyou.forgeboot.security.core.common.builder.SecurityFilterChainRegistrar
import com.gewuyou.forgeboot.security.core.common.customizer.HttpSecurityCustomizer
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
@ConditionalOnProperty(name = ["spring.main.web-application-type"], havingValue = "servlet", matchIfMissing = true)
class ServletAuthorizeSecurityConfiguration(
    private val accessManager: AccessManager,
    private val permissionResolver: PermissionResolver,
) {
    /**
     * 创建默认的安全过滤链，适用于 Servlet 编程模型
     *
     * @param registrar 安全过滤链注册器
     * @param http HttpSecurity 配置对象
     * @return 构建后的安全过滤链实例
     * @throws Exception 构建过程中可能抛出的异常
     */
    @Bean(name = ["defaultSecurityFilterChain"])
    @ConditionalOnBean(SecurityFilterChainRegistrar::class)
    @Throws(Exception::class)
    fun defaultSecurityFilterChain(
        registrar: SecurityFilterChainRegistrar,
        http: HttpSecurity,
    ): SecurityFilterChain = registrar.buildChain(
        "default",
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