package com.gewuyou.forgeboot.security.core.common.registrar

import com.gewuyou.forgeboot.security.core.common.wrapper.SecurityFilterChainWrapper
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.util.matcher.RequestMatcher

/**
 * 安全过滤器链注册商
 *
 * 该函数接口用于定义构建安全过滤器链的契约，通过提供唯一的链标识、基础配置对象、请求匹配规则以及配置逻辑，
 * 可以创建一个定制化的安全过滤器链实例。
 *
 * @since 2025-06-24 16:07:19
 * @author gewuyou
 */
fun interface SecurityFilterChainRegistrar {
    /**
     * 构建安全过滤器链
     *
     * 此方法用于基于提供的匹配器和配置逻辑创建一个定制化的安全过滤器链。
     * 方法内部应实现将给定的配置应用到 HTTP 安全对象，并根据 matcher 决定是否启用该链。
     *
     * @param chainId 唯一标识此过滤器链的字符串，用于后续引用或管理
     * @param http 提供基础配置的 HttpSecurity 对象，用于构建 Web 安全策略
     * @param matcher 用于匹配请求的 RequestMatcher 对象，决定是否应用该过滤器链
     * @param config 用于配置安全策略的函数，接受 HttpSecurity 对象作为参数进行自定义配置
     * @return 返回构建完成的 SecurityFilterChainWrapper 实例，封装了最终的安全过滤器链
     */
    fun buildChain(
        chainId: String,
        http: HttpSecurity,
        matcher: RequestMatcher,
        config: (HttpSecurity) -> Unit
    ): SecurityFilterChainWrapper
}