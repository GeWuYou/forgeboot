package com.gewuyou.forgeboot.security.authorize.api.resolver

/**
 * 权限解析程序接口，用于将请求路径和HTTP方法转换为具体的权限标识符。
 *
 * @since 2025-06-24 13:23:29
 * @author gewuyou
 */
fun interface PermissionResolver {

    /**
     * 解析给定的请求路径和HTTP方法，生成对应的权限标识字符串。
     *
     * 此方法通常用于安全框架中，用来判断用户是否拥有执行特定操作的权限。
     *
     * @param path 请求的URL路径，例如 "/api/users"
     * @param method HTTP请求方法，如 GET, POST 等
     * @return 返回一个表示权限的字符串，如 "user:read", "user:write" 等
     */
    fun resolve(path: String, method: String): String
}