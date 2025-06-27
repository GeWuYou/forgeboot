package com.gewuyou.forgeboot.security.authenticate.impl.registry

import com.gewuyou.forgeboot.security.authenticate.api.registry.LoginRequestTypeRegistry
import com.gewuyou.forgeboot.security.core.authenticate.entities.request.LoginRequest

/**
 * 简单登录请求类型注册表
 *
 * 用于管理不同登录类型的请求类映射关系，支持动态注册和获取登录请求类型。
 *
 * 示例代码（Kotlin）：
 * ```kt
 * @Bean
 * fun loginRequestTypeRegistry(): LoginRequestTypeRegistry {
 *     return LoginRequestTypeRegistry().apply {
 *         register("default", DefaultLoginRequest::class.java)
 *         register("sms", SmsLoginRequest::class.java)
 *     }
 * }
 * ```
 *
 * 示例代码（Java）：
 * ```java
 * @Bean
 * public LoginRequestTypeRegistry loginRequestTypeRegistry() {
 *    return new DefaultLoginRequestTypeRegistry()
 *       .register("default", DefaultLoginRequest.class)
 *       .register("sms", SmsLoginRequest.class);
 * }
 * ```
 * ⚠️ 注意：此注册表不是线程安全的，建议仅用于应用启动期间进行注册，不适合运行时动态变更
 * @since 2025-06-10 16:33:43
 * @author gewuyou
 */
class SimpleLoginRequestTypeRegistry : LoginRequestTypeRegistry {

    /**
     * 内部使用可变Map保存登录类型与对应LoginRequest子类的映射关系
     */
    private val mapping = mutableMapOf<String, Class<out LoginRequest>>()

    /**
     * 注册指定登录类型对应的请求类
     *
     * 此方法允许将特定的登录类型字符串与相应的LoginRequest实现类进行绑定，
     * 以便后续可以通过登录类型标识符动态解析出对应的请求类。
     *
     * @param loginType 登录类型的标识字符串
     * @param clazz 继承自LoginRequest的具体请求类
     * @return 返回当前注册表实例，以支持链式调用
     */
    override fun register(loginType: String, clazz: Class<out LoginRequest>): LoginRequestTypeRegistry {
        mapping[loginType] = clazz
        return this
    }

    /**
     * 根据登录类型获取对应的请求类
     *
     * 此方法用于查找之前通过register方法注册的登录类型所对应的LoginRequest子类。
     * 如果未找到匹配的登录类型，则返回null。
     *
     * @param loginType 登录类型的标识字符串
     * @return 返回对应的LoginRequest子类，若未找到则返回null
     */
    override fun getTypeForLoginType(loginType: String): Class<out LoginRequest>? {
        return mapping[loginType]
    }
}