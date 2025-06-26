package com.gewuyou.forgeboot.security.core.authorize.entities

import org.springframework.security.core.GrantedAuthority

/**
 * API 密钥主体，用于存储认证后的API密钥相关信息。
 *
 * @property principal 认证主体标识，通常是API密钥字符串
 * @property authorities 与此API密钥关联的权限列表
 *
 * @since 2025-06-25 13:11:37
 * @author gewuyou
 */
data class SingleTokenPrincipal (
    val principal: String,
    val authorities: List<GrantedAuthority>
)