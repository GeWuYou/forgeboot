package com.gewuyou.forgeboot.security.authorize.api.core.manager

import org.springframework.security.core.Authentication

/**
 * 访问管理器接口，用于定义鉴权逻辑
 *
 * @since 2025-06-15 18:38:07
 * @author gewuyou
 */
fun interface AccessManager {
    /**
     * 判断用户是否具有指定权限
     *
     * @param authentication Spring Security提供的身份认证信息
     * @param permission 需要验证的权限字符串
     * @return Boolean 返回是否有权限的布尔值
     */
    fun hasPermission(authentication: Authentication, permission: String): Boolean
}