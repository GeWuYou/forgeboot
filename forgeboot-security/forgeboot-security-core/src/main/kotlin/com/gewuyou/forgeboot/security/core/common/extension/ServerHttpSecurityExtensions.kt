package com.gewuyou.forgeboot.security.core.common.extension

import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

/**
 * 清理不需要的配置，关闭清单如下：
 *
 * 禁用以下 Spring Security 功能以简化安全配置：
 * - CSRF 保护
 * - 表单登录认证
 * - 匿名用户支持
 * - 请求缓存
 * - HTTP Basic 认证
 * - 登出功能
 *
 * 同时设置一个无操作的安全上下文仓库，适用于无需持久化安全上下文的场景。
 *
 * @return 返回配置修改后的 [ServerHttpSecurity] 实例
 */
fun ServerHttpSecurity.cleanUnNeedConfig(): ServerHttpSecurity {
    return this
        .csrf { it.disable() }
        .formLogin { it.disable() }
        .anonymous { it.disable() }
        .requestCache { it.disable() }
        .httpBasic { it.disable() }
        .logout { it.disable() }
        .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
}