package com.gewuyou.forgeboot.security.authorize.api.core.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Security 授权配置属性类，用于定义安全相关的可配置项
 *
 * 该类通过@ConfigurationProperties绑定配置前缀"forgeboot.security.authorize"，
 * 提供了默认异常响应消息和API密钥启用状态的配置支持。
 *
 * @property defaultExceptionResponse 当访问被拒绝时返回的默认提示信息
 * @property apiKey API密钥相关配置属性对象
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
     * API密钥相关配置属性对象，包含是否启用API密钥验证的开关
     */
    var apiKey: ApiKeyProperties = ApiKeyProperties()

    /**
     * API密钥功能的子配置类，用于控制API密钥验证的启用状态
     *
     * @property enabled 是否启用API密钥验证功能，默认为false
     */
    class ApiKeyProperties {
        /**
         * 控制是否启用API密钥验证功能，默认值为false
         */
        var enabled: Boolean = false
        var pathPatterns: List<String> = listOf("/api/**")
        var useAuthorizationManager: Boolean = true
    }
}