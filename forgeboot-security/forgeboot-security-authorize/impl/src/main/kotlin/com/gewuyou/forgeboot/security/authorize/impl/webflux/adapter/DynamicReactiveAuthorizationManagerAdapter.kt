package com.gewuyou.forgeboot.security.authorize.impl.webflux.adapter

import com.gewuyou.forgeboot.security.authorize.api.core.manager.AccessManager
import com.gewuyou.forgeboot.security.authorize.api.webflux.manager.DynamicReactiveAuthorizationManager
import com.gewuyou.forgeboot.security.authorize.api.core.resolver.PermissionResolver
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authorization.AuthorizationContext
import reactor.core.publisher.Mono


/**
 * 反应式授权管理器适配器
 * 用于将访问控制逻辑适配为 Spring Security 的 ReactiveAuthorizationManager 接口规范
 *
 * @property accessManager 访问控制管理器，用于执行具体的权限判断逻辑
 * @property permissionResolver 权限解析器，用于从请求中动态解析所需权限
 * @since 2025-06-24 13:13:40
 * @author gewuyou
 */
class DynamicReactiveAuthorizationManagerAdapter(
    private val accessManager: AccessManager,
    private val permissionResolver: PermissionResolver
) : DynamicReactiveAuthorizationManager<AuthorizationContext> {

    /**
     * 检查当前请求是否满足所需的授权条件
     *
     * 此方法从认证信息和授权上下文中提取关键数据，通过权限解析器获取所需权限，
     * 并使用访问控制管理器验证用户是否具备该权限。
     *
     * @param authentication 当前的认证信息流，包含用户身份和权限
     * @param context 授权上下文，包含请求和交换信息
     * @return Mono<AuthorizationDecision> 返回一个授权决策对象的流
     */
    override fun check(
        authentication: Mono<Authentication>,
        context: AuthorizationContext
    ): Mono<AuthorizationDecision> {
        return authentication
            .map { auth ->
                // 获取请求相关的信息
                val exchange = context.exchange
                val request = exchange.request

                // 提取请求路径和方法
                val path = request.path.toString()
                val method = request.method

                // 动态获取要校验的权限（可从 path、header、注解、上下文等推断）
                val requiredPermission = permissionResolver.resolve(path, method.name())

                // 判断用户是否拥有该权限
                val granted = accessManager.hasPermission(auth, requiredPermission)
                // 创建授权决策结果
                AuthorizationDecision(granted)
            }
            .defaultIfEmpty(AuthorizationDecision(false))
    }
}
