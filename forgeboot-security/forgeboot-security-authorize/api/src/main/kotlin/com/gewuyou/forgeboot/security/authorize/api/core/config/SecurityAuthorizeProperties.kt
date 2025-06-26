package com.gewuyou.forgeboot.security.authorize.api.core.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Security 授权配置属性类，用于定义安全相关的可配置项
 *
 * 该类通过@ConfigurationProperties绑定配置前缀"forgeboot.security.authorize"，
 * 提供了默认异常响应消息、单Token控制及相关路径模式的配置支持。
 *
 * @property defaultExceptionResponse 当访问被拒绝时返回的默认提示信息
 * @property singleToken 单Token配置属性对象，包含启用状态、匹配路径及是否使用授权管理器
 *
 * @since 2025-06-15 19:26:20
 * @author gewuyou
 */
@ConfigurationProperties(prefix = "forgeboot.security.authorize")
class SecurityAuthorizeProperties {
    /**
     * 默认的访问拒绝响应消息，用于在未授权访问时返回给客户端
     */
    var defaultExceptionResponse: String = "Sorry, you don't have access to the resource!"

    /**
     * 单Token配置属性实例，用于定义Token相关的行为和路径匹配规则
     */
    var singleToken: SingleTokenProperties = SingleTokenProperties()

    /**
     * 单Token配置内部类，封装与Token验证行为相关的配置项
     *
     * 用于控制特定路径下的Token验证行为，包括启用状态、路径匹配模式以及是否使用授权管理器。
     */
    class SingleTokenProperties {
        /**
         * 控制是否启用API密钥验证功能，默认值为false
         */
        var enabled: Boolean = false
        /**
         * 指定是否通过Spring Security的AuthorizationManager进行权限决策，默认为true
         */
        var useAuthorizationManager: Boolean = true

        /**
         * 定义需要应用Token验证的请求路径模式列表
         */
        var pathPatterns: List<String> = listOf("/api/**")
    }
}