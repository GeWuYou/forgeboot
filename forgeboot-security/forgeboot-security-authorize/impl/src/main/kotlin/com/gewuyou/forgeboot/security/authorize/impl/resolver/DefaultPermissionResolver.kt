package com.gewuyou.forgeboot.security.authorize.impl.resolver

import com.gewuyou.forgeboot.security.authorize.api.resolver.PermissionResolver

/**
 * 默认权限解析程序
 *
 * @since 2025-06-24 13:42:20
 * @author gewuyou
 */
class DefaultPermissionResolver : PermissionResolver {

    /**
     * 将请求路径和HTTP方法解析为标准化的权限标识符
     *
     * 此方法会对原始路径进行标准化处理，替换其中的动态参数部分为统一占位符，
     * 然后将HTTP方法和标准化后的路径组合成最终的权限标识符。
     *
     * @param path 请求路径
     * @param method HTTP请求方法
     * @return 标准化的权限标识符，格式为"METHOD:/path/template"
     */
    override fun resolve(path: String, method: String): String {
        // 对路径进行标准化处理：
        // 1. 转换为小写
        // 2. 替换数字ID为{id}通配符
        // 3. 替换MongoDB ObjectId为{objectId}
        // 4. 替换UUID为{uuid}
        val normalizedPath = path
            .lowercase()
            .replace(Regex("/\\d+"), "/{id}")
            .replace(Regex("/[a-f0-9]{24}"), "/{objectId}")
            .replace(Regex("/[a-z0-9\\-]{36}"), "/{uuid}")

        return "${method}:$normalizedPath"
    }
}