package com.gewuyou.forgeboot.security.authorize.impl.core.manager

import com.gewuyou.forgeboot.security.authorize.api.core.manager.AccessManager
import com.gewuyou.forgeboot.security.authorize.api.core.provider.PermissionProvider
import com.gewuyou.forgeboot.security.authorize.api.core.strategy.AuthorizationStrategy
import org.springframework.security.core.Authentication

/**
 *默认 Access Manager
 *
 * @since 2025-06-15 18:41:22
 * @author gewuyou
 */
class DefaultAccessManager(
    private val providers: List<PermissionProvider>,
    private val strategy : AuthorizationStrategy
) : AccessManager {
    /**
     * 判断用户是否具有指定权限
     *
     * @param authentication Spring Security提供的身份认证信息
     * @param permission 需要验证的权限字符串
     * @return Boolean 返回是否有权限的布尔值
     */
    override fun hasPermission(authentication: Authentication, permission: String): Boolean =
        strategy.authorize(authentication, permission, providers)

}
