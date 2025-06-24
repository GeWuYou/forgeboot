package com.gewuyou.forgeboot.security.authorize.autoconfigure

import com.gewuyou.forgeboot.security.authorize.api.manager.AccessManager
import com.gewuyou.forgeboot.security.authorize.api.provider.PermissionProvider
import com.gewuyou.forgeboot.security.authorize.api.resolver.PermissionResolver
import com.gewuyou.forgeboot.security.authorize.api.strategy.AuthorizationStrategy
import com.gewuyou.forgeboot.security.authorize.impl.manager.DefaultAccessManager
import com.gewuyou.forgeboot.security.authorize.impl.resolver.DefaultPermissionResolver
import com.gewuyou.forgeboot.security.authorize.impl.strategy.AnyMatchStrategy
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Forge Security Authorize 核心配置
 *
 * 该配置类定义了权限控制所需的核心组件Bean，包括权限解析器、授权策略和访问管理器。
 * 这些Bean用于实现基于权限表达式的访问控制机制。
 *
 * @since 2025-06-24 16:49:37
 * @author gewuyou
 */
@Configuration
class ForgeSecurityAuthorizeCoreConfiguration {
    /**
     * 权限解析器Bean，用于将权限表达式解析为具体的权限对象。
     *
     * 权限解析器是访问控制的基础组件，负责解析配置中的权限表达式，
     * 将其转换为系统可识别和处理的权限对象。
     *
     * @return 返回一个具体的权限解析器实例，此处为DefaultPermissionResolver。
     *         该实例实现了基本的权限解析功能。
     */
    @Bean
    @ConditionalOnBean
    fun permissionResolver(): PermissionResolver {
        return DefaultPermissionResolver()
    }

    /**
     * 授权策略Bean，用于定义权限匹配的策略。
     *
     * 授权策略决定了如何根据用户权限与资源所需权限进行匹配，
     * 是决定访问是否被允许的关键逻辑所在。
     *
     * @return 返回一个具体授权策略实例，此处为AnyMatchStrategy（任意匹配策略）。
     *         该策略表示只要存在任意一个匹配的权限即可允许访问。
     */
    @Bean
    @ConditionalOnBean
    fun authorizationStrategy(): AuthorizationStrategy {
        return AnyMatchStrategy()
    }

    /**
     * 访问管理器Bean，负责处理访问控制决策。
     *
     * 访问管理器是整个权限控制的核心组件，它整合了权限提供者和授权策略，
     * 在访问请求到来时，使用权限提供者获取权限信息，并通过策略进行决策。
     *
     * @param providers 权限提供者列表，用于获取不同资源所需的权限信息。
     *                  每个Provider通常对应一种资源类型或数据来源。
     * @param strategy 授权策略实例，用于决定是否允许访问。
     *                 该策略将影响最终访问控制决策。
     *
     * @return 返回一个AccessManager的具体实现，此处为DefaultAccessManager。
     *         该实现组合了权限提供者和授权策略，完成了完整的访问控制流程。
     */
    @Bean
    @ConditionalOnBean
    fun accessManager(
        providers: List<PermissionProvider>,
        strategy: AuthorizationStrategy,
    ): AccessManager {
        return DefaultAccessManager(providers, strategy)
    }
}