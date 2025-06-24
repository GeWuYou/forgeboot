package com.gewuyou.forgeboot.security.authenticate.api.customizer

import org.springframework.security.config.annotation.web.builders.HttpSecurity

/**
 * 登录过滤器定制器接口
 *
 * 该函数式接口用于自定义Spring Security的登录过滤器配置。
 * 实现此接口的类或lambda表达式可用于调整安全配置中的HttpSecurity设置，
 * 特别是在登录认证流程中对请求的安全策略进行定制化处理。
 *
 * @since 2025-06-11 18:53:25
 * @author gewuyou
 */
fun interface LoginFilterCustomizer {
    /**
     * 自定义方法，用于配置或修改HttpSecurity对象。
     *
     * 此方法将被调用以应用特定的安全性规则到登录过滤器上。
     *
     * @param http HttpSecurity对象，提供了配置Web安全性的API
     */
    fun customize(http: HttpSecurity)
}