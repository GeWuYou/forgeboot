package com.gewuyou.forgeboot.security.authenticate.api.registry

import com.gewuyou.forgeboot.security.core.authenticate.entities.request.LoginRequest

/**
 * 登录请求类型注册表
 *
 * 用于管理不同登录类型的请求类，支持注册和查询操作。
 *
 * @since 2025-06-26 22:06:00
 * @author gewuyou
 */
interface LoginRequestTypeRegistry {

    /**
     * 注册一个新的登录类型及其对应的请求类。
     *
     * @param loginType 登录类型的标识字符串
     * @param clazz       继承自LoginRequest的具体请求类
     */
    fun register(loginType: String, clazz: Class<out LoginRequest>): LoginRequestTypeRegistry

    /**
     * 根据登录类型获取对应的请求类。
     *
     * @param loginType 登录类型的标识字符串
     * @return 返回与登录类型关联的请求类，如果未找到则返回null
     */
    fun getTypeForLoginType(loginType: String): Class<out LoginRequest>?
}