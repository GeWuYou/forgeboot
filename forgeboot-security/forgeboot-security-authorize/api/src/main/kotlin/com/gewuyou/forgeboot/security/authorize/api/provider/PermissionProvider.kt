package com.gewuyou.forgeboot.security.authorize.api.provider

import org.springframework.security.core.Authentication

/**
 * 权限提供程序接口，用于定义权限集合的生成逻辑。
 *
 * @since 2025-06-15 13:08:18
 * @author gewuyou
 */
fun interface PermissionProvider {

    /**
     * 提供基于认证信息的权限集合。
     *
     * @param authentication 包含用户认证信息的对象，用于权限判断
     * @return 返回与当前认证信息关联的权限字符串集合
     */
    fun listPermissions(authentication: Authentication): Collection<String>
}