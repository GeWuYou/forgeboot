package com.gewuyou.forgeboot.security.authenticate.api.registry

import com.gewuyou.forgeboot.security.core.authenticate.entities.request.LoginRequest

/**
 * 登录请求类型注册表
 *
 * 用于管理不同登录类型的请求类映射关系，支持动态注册和获取登录请求类型。
 *
 * ```kt
 * @Bean
 * fun loginRequestTypeRegistry(): LoginRequestTypeRegistry {
 *     return LoginRequestTypeRegistry().apply {
 *         register("default", DefaultLoginRequest::class.java)
 *         register("sms", SmsLoginRequest::class.java)
 *     }
 * }
 * ```
 * @since 2025-06-10 16:33:43
 * @author gewuyou
 */
class LoginRequestTypeRegistry {
    /**
     * 内部使用可变Map保存登录类型与对应LoginRequest子类的映射关系
     */
    private val mapping = mutableMapOf<String, Class<out LoginRequest>>()

    /**
     * 注册指定登录类型对应的请求类
     *
     * @param loginType 登录类型的标识字符串
     * @param clazz     继承自LoginRequest的具体请求类
     */
    fun register(loginType: String, clazz: Class<out LoginRequest>) {
        mapping[loginType] = clazz
    }

    /**
     * 根据登录类型获取对应的请求类
     *
     * @param loginType 登录类型的标识字符串
     * @return 返回对应的LoginRequest子类，若未找到则返回null
     */
    fun getTypeForLoginType(loginType: String): Class<out LoginRequest>? {
        return mapping[loginType]
    }
}