package com.gewuyou.forgeboot.security.authorize.impl.core.provider

import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.security.authorize.api.core.provider.PermissionProvider
import org.springframework.security.core.Authentication

/**
 *抽象权限提供程序
 *
 * @since 2025-06-23 21:45:49
 * @author gewuyou
 */
abstract class AbstractPermissionProvider : PermissionProvider {

    /**
     * 获取权限集合模板方法
     */
    override fun listPermissions(authentication: Authentication): Collection<String> {
        if (!supports(authentication)) return emptySet()
        return try {
            doProvide(authentication).toSet()
        } catch (ex: Exception) {
            log.error("获取权限失败", ex)
            emptySet()
        }
    }

    /**
     * 子类实现：获取权限的具体逻辑
     */
    protected abstract fun doProvide(authentication: Authentication): Collection<String>

    /**
     * 是否支持当前 Authentication 类型
     */
    protected open fun supports(authentication: Authentication): Boolean = true
}