package com.gewuyou.forgeboot.security.authorize.impl.servlet.adapter

import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.security.authorize.api.core.manager.AccessManager
import com.gewuyou.forgeboot.security.authorize.api.core.resolver.PermissionResolver
import com.gewuyou.forgeboot.security.authorize.api.servlet.manager.DynamicAuthorizationManager
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.core.Authentication
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import java.util.function.Supplier

/**
 * 授权管理器适配器，用于将 AccessManager 适配为 Spring Security 的 AuthorizationManager。
 *
 * @property accessManager 提供权限判断的访问管理器实例
 * @property permissionResolver 用于解析请求路径和方法对应的权限需求
 * @since 2025-06-23 21:42:19
 * @author gewuyou
 */
class DynamicAuthorizationManagerAdapter(
    private val accessManager: AccessManager,
    private val permissionResolver: PermissionResolver,
) : DynamicAuthorizationManager<RequestAuthorizationContext> {

    /**
     * 执行权限校验的核心方法。
     *
     * @param authentication 提供认证信息的 Supplier，从中获取当前用户的身份认证对象
     * @param context 请求上下文，包含请求相关的额外信息（未在此实现中使用）
     * @return 返回一个 AuthorizationDecision 对象，表示是否通过授权
     */
    override fun check(
        authentication: Supplier<Authentication>,
        context: RequestAuthorizationContext,
    ): AuthorizationDecision {
        try {
            // 获取 HTTP 请求的基本信息
            val request = context.request
            val method = request.method
            val path = request.requestURI
            // 解析当前请求所需的权限
            val requiredPermission = permissionResolver.resolve(path, method)

            // 校验用户是否拥有该权限
            val granted = accessManager.hasPermission(authentication.get(), requiredPermission)

            // 返回授权决策结果
            return AuthorizationDecision(granted)
        } catch (e: Exception) {
            log.error("权限校验异常", e)
            return AuthorizationDecision(false)
        }
    }
}