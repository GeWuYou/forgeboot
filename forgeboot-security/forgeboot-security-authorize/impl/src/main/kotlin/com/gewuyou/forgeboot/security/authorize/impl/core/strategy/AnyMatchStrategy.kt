package com.gewuyou.forgeboot.security.authorize.impl.core.strategy

import com.gewuyou.forgeboot.security.authorize.api.core.provider.PermissionProvider
import com.gewuyou.forgeboot.security.authorize.api.core.strategy.AuthorizationStrategy
import org.springframework.security.core.Authentication

/**
 * 任何匹配策略 - 实现授权策略接口，采用任意权限匹配机制
 *
 * @since 2025-06-15 18:51:23
 * @author gewuyou
 */
class AnyMatchStrategy : AuthorizationStrategy {
    /**
     * 执行授权验证
     *
     * @param authentication 当前用户身份认证信息
     * @param permission 需要验证的权限标识
     * @param providers 权限提供者列表
     * @return Boolean 返回授权结果，true表示授权通过，false表示拒绝访问
     *
     * 该方法通过遍历所有权限提供者，只要有一个提供者包含目标权限即视为授权成功
     */
    override fun authorize(
        authentication: Authentication,
        permission: String,
        providers: List<PermissionProvider>
    ) = providers.any { permission in it.listPermissions(authentication) }
}