package com.gewuyou.forgeboot.security.authorize.autoconfigure.webflux

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.security.authorize.api.core.config.SecurityAuthorizeProperties
import com.gewuyou.forgeboot.security.authorize.api.core.manager.AccessManager
import com.gewuyou.forgeboot.security.authorize.api.core.resolver.PermissionResolver
import com.gewuyou.forgeboot.security.authorize.impl.webflux.adapter.DynamicReactiveAuthorizationManagerAdapter
import com.gewuyou.forgeboot.security.authorize.impl.webflux.handler.ReactiveAuthorizationExceptionHandler
import com.gewuyou.forgeboot.security.authorize.impl.webflux.registrar.StatelessSecurityWebFilterChainRegistrar
import com.gewuyou.forgeboot.security.core.common.constants.SecurityConstants
import com.gewuyou.forgeboot.security.core.common.customizer.ServerHttpSecurityCustomizer
import com.gewuyou.forgeboot.security.core.common.registrar.SecurityWebFilterChainRegistrar
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.ReactiveAuthenticationManagerAdapter
import org.springframework.security.authorization.ReactiveAuthorizationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authorization.AuthorizationContext
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers

/**
 * 反应式授权配置
 * 配置响应式安全过滤链和相关组件，用于处理WebFlux环境下的安全控制。
 *
 * @since 2025-06-24 16:48:11
 * @author gewuyou
 */
@Configuration(proxyBeanMethods = false)
@EnableWebFluxSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
class ReactiveAuthorizeSecurityConfiguration(
    private val accessManager: AccessManager,
    private val permissionResolver: PermissionResolver,
) {

    /**
     * 创建默认的安全过滤链，适用于响应式编程模型
     * 使用给定的注册器和配置构建一个默认的安全过滤链，该链拒绝所有请求。
     *
     * @param registrar 安全过滤链注册器，用于注册新的安全过滤链
     * @param http ServerHttpSecurity配置对象，用于定义安全规则
     * @return 构建后的安全过滤链实例
     * @throws Exception 构建过程中可能抛出的异常
     */
    @Bean(name = ["defaultSecurityWebFilterChain"])
    @ConditionalOnBean
    @Throws(Exception::class)
    fun defaultSecurityWebFilterChain(
        registrar: SecurityWebFilterChainRegistrar,
        http: ServerHttpSecurity,
    ): SecurityWebFilterChain = registrar
        .buildChain(
            SecurityConstants.DEFAULT_CHAIN_ID,
            http,
            ServerWebExchangeMatchers.anyExchange()
        ) { it: ServerHttpSecurity ->
            it.authorizeExchange {
                it.anyExchange().denyAll()
            }
        }

    /**
     * 注册响应式授权异常处理器
     * 创建并返回一个用于处理授权失败情况的异常处理器。
     *
     * @param objectMapper 用于序列化错误响应的对象映射器
     * @param securityAuthorizeProperties 授权配置属性，包含自定义的授权设置
     * @return ServerAccessDeniedHandler 实例，用于处理访问被拒绝的情况
     */
    @Bean
    @ConditionalOnClass(ServerAccessDeniedHandler::class)
    @ConditionalOnBean
    fun reactiveAuthorizationExceptionHandler(
        objectMapper: ObjectMapper,
        securityAuthorizeProperties: SecurityAuthorizeProperties,
    ): ServerAccessDeniedHandler {
        return ReactiveAuthorizationExceptionHandler(objectMapper, securityAuthorizeProperties)
    }

    /**
     * 动态响应式授权管理器适配器
     * 创建并返回一个适配后的响应式授权管理器实例，用于动态评估访问请求。
     *
     * @return 适配后的响应式授权管理器实例
     */
    @Bean
    @ConditionalOnBean
    fun dynamicReactiveAuthorizationManager(): ReactiveAuthorizationManager<AuthorizationContext> {
        return DynamicReactiveAuthorizationManagerAdapter(accessManager, permissionResolver)
    }

    /**
     * 反应式认证管理器
     * 创建并返回一个主认证管理器实例，用于处理响应式编程环境中的身份验证请求。
     * 该管理器基于提供的认证提供者列表进行初始化。
     *
     * 此方法通过将传统的 ProviderManager 包装在 ReactiveAuthenticationManagerAdapter 中，
     * 实现了对响应式编程模型的支持。ProviderManager 被构造为使用给定的认证提供者列表，
     * 并作为适配器的一部分来完成异步的身份验证逻辑。
     *
     * @param authenticationProviders 认证提供者列表，用于执行具体的身份验证逻辑。
     *                                每个提供者负责特定类型的身份验证机制。
     * @return 初始化后的 ReactiveAuthenticationManager 实例，用于响应式环境下的身份验证流程。
     *         返回的对象是 ReactiveAuthenticationManagerAdapter 的实例，其内部封装了同步的 ProviderManager。
     */
    @Bean
    @Primary
    @ConditionalOnBean
    fun reactiveAuthenticationManager(
        authenticationProviders: List<AuthenticationProvider>
    ): ReactiveAuthenticationManager {
        return ReactiveAuthenticationManagerAdapter(
            ProviderManager(authenticationProviders)
        )
    }

    /**
     * 无状态安全过滤链注册器
     * 创建并返回一个无状态的安全过滤链注册器，用于注册需要无状态处理的安全过滤链。
     *
     * @param reactiveAuthorizationManager 响应式授权管理器，用于评估访问请求
     * @param reactiveAccessDeniedHandler 授权失败处理器，用于处理访问被拒绝的情况
     * @param serverHttpSecurityCustomizer 安全配置定制器列表，用于自定义安全配置
     * @return 安全过滤链注册器实例，用于注册无状态安全过滤链
     */
    @Bean
    @ConditionalOnBean
    fun statelessSecurityWebFilterChainRegistrar(
        reactiveAuthorizationManager: ReactiveAuthorizationManager<AuthorizationContext>,
        reactiveAccessDeniedHandler: ServerAccessDeniedHandler,
        serverHttpSecurityCustomizer: List<ServerHttpSecurityCustomizer>,
    ): SecurityWebFilterChainRegistrar {
        return StatelessSecurityWebFilterChainRegistrar(
            serverHttpSecurityCustomizer,
            reactiveAuthorizationManager,
            reactiveAccessDeniedHandler
        )
    }
}