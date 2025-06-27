package com.gewuyou.forgeboot.security.authenticate.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpMethod

/**
 * 安全身份验证属性配置类
 *
 * 该类用于定义安全认证相关的配置属性，包含路径权限设置、异常响应信息和基础URL配置。
 * 属性值通过@ConfigurationProperties绑定前缀"forgeboot.security.authenticate"的配置项。
 *
 * @since 2025-06-11 15:20:27
 * @author gewuyou
 */
@ConfigurationProperties(prefix = "forgeboot.security.authenticate")
class SecurityAuthenticateProperties {
    /**
     * 登录请求的HTTP方法，默认为POST
     */
    var loginHttpMethod: String = HttpMethod.POST.name()

    /**
     * 默认认证异常响应内容，当请求缺少认证时返回此字符串
     */
    var defaultExceptionResponse = "Full authentication is required to access this resource"

    /**
     * 默认认证失败响应内容，当认证失败时返回此字符串
     */
    var defaultAuthenticationFailureResponse = "If the authentication fails, please report the request ID for more information!"

    /**
     * 认证模块的基础URL前缀
     */
    var baseUrl = "/api/auth"

    /**
     * 登录接口的URL路径
     */
    var loginUrl = "/login"

    /**
     * 登出接口的URL路径
     */
    var logoutUrl = "/logout"

    /**
     * 是否启用单 token 登录认证（如登录接口、token 生成器）
     */
    var singleToken: SingleTokenAuthenticationProperties = SingleTokenAuthenticationProperties()

    /**
     * 配置单 Token 认证相关属性
     *
     * 提供一个嵌套类来管理单 Token 认证的启用状态，这种设计模式允许在不改变主配置类的情况下，
     * 独立扩展单 Token 相关的更多配置项。
     */
    class SingleTokenAuthenticationProperties {
        var enabled: Boolean = false
    }

    /**
     * 是否启用双 token 登录认证
     */
    var dualToken: DualTokenAuthenticationProperties = DualTokenAuthenticationProperties()

    /**
     * 配置双 Token 认证相关属性
     *
     * 嵌套类用于封装双 Token 认证机制的启用状态，保持配置结构清晰并支持未来扩展。
     */
    class DualTokenAuthenticationProperties {
        var enabled: Boolean = false
    }
}