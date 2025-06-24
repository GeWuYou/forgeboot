package com.gewuyou.forgeboot.security.core.common.extension

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.context.NullSecurityContextRepository

/**
 * 清理不需要的配置，禁用指定的安全功能模块。
 *
 * 此方法用于关闭一系列默认的安全配置项，适用于无状态（stateless）认证场景，
 * 例如基于Token或API密钥的身份验证架构。
 *
 * @return 返回当前 HttpSecurity 实例，用于链式调用
 */
fun HttpSecurity.cleanUnNeedConfig(): HttpSecurity {
    return this
        /**
         * 禁用 CSRF 保护机制。
         * 在无状态 API 场景中通常不需要 CSRF 保护。
         */
        .csrf { it.disable() }
        /**
         * 禁用表单登录认证机制。
         * 表单登录不适用于 RESTful API 或 Token 认证场景。
         */
        .formLogin { it.disable() }
        /**
         * 设置 SecurityContextRepository 为 NullSecurityContextRepository。
         * 避免将安全上下文存储到 HTTP 请求中。
         */
        .securityContext { it.securityContextRepository(NullSecurityContextRepository()) }
        /**
         * 设置会话创建策略为 STATELESS。
         * 表示应用不会创建或使用 HTTP 会话进行身份识别。
         */
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        /**
         * 禁用匿名用户访问支持。
         * 所有未认证用户将被视为无权限。
         */
        .anonymous { it.disable() }
        /**
         * 禁用请求缓存。
         * 在无状态认证流程中不需要缓存请求。
         */
        .requestCache { it.disable() }
        /**
         * 禁用 HTTP Basic 认证。
         * 使用 Token 或其他自定义认证方式时可安全禁用此项。
         */
        .httpBasic { it.disable() }
        /**
         * 禁用 Remember-Me 自动登录功能。
         * 无状态认证场景下此功能不再适用。
         */
        .rememberMe { it.disable() }
}