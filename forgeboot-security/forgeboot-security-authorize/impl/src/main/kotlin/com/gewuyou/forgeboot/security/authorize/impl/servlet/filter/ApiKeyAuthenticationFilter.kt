package com.gewuyou.forgeboot.security.authorize.impl.servlet.filter

import com.gewuyou.forgeboot.security.core.common.constants.SecurityConstants
import com.gewuyou.forgeboot.security.core.common.token.ApiKeyAuthenticationToken
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

/**
 * API 密钥身份验证筛选器
 * 用于处理基于API密钥的身份验证流程，继承自OncePerRequestFilter以确保每个请求只被过滤一次。
 *
 * @param authenticationManager 身份验证管理器，用于执行实际的身份验证操作
 * @since 2025-06-25 13:34:47
 * @author gewuyou
 */
class ApiKeyAuthenticationFilter(
    private val authenticationManager: AuthenticationManager
) : OncePerRequestFilter() {

    /**
     * 执行内部过滤逻辑
     * 从请求头中提取API密钥并进行身份验证
     *
     * @param request 当前HTTP请求
     * @param response 当前HTTP响应
     * @param chain 过滤器链
     */
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        // 从请求头中获取Authorization字段
        val header = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER)
        
        // 检查是否为Bearer类型认证信息
        if (header?.startsWith(SecurityConstants.BEARER_PREFIX) == true) {
            // 提取并清理API密钥
            val apiKey = header.removePrefix(SecurityConstants.BEARER_PREFIX).trim()
            
            // 创建认证令牌
            val token = ApiKeyAuthenticationToken(apiKey, null)
            
            // 执行认证并存储认证结果到安全上下文中
            val authResult = authenticationManager.authenticate(token)
            SecurityContextHolder.getContext().authentication = authResult
        }
        // 继续执行过滤器链
        chain.doFilter(request, response)
    }
}