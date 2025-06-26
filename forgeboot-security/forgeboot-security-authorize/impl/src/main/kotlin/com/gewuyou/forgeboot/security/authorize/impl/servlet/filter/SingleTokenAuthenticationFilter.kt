package com.gewuyou.forgeboot.security.authorize.impl.servlet.filter

import com.gewuyou.forgeboot.security.core.common.constants.SecurityConstants
import com.gewuyou.forgeboot.security.core.common.token.SingleTokenAuthenticationToken
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

/**
 * 单Token认证过滤器
 *
 * 用于在请求处理链中执行基于Token的身份验证逻辑。
 * 从请求头中提取指定格式的Token并构造未认证身份令牌放入安全上下文。
 *
 * @since 2025-06-25 13:34:47
 * @author gewuyou
 */
class SingleTokenAuthenticationFilter() : OncePerRequestFilter() {

    /**
     * 执行内部过滤逻辑
     *
     * 从请求头中提取API密钥，解析出Token后构造未认证的SingleTokenAuthenticationToken，
     * 并将其设置到SecurityContextHolder上下文中。若当前上下文已存在认证信息，则直接跳过。
     *
     * @param request 当前HTTP请求
     * @param response 当前HTTP响应
     * @param chain 过滤器链，用于继续执行后续的过滤操作
     */
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        // 如果已有认证信息，跳过
        if (SecurityContextHolder.getContext().authentication != null) {
            chain.doFilter(request, response)
            return
        }

        val header = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER)
        if (header?.startsWith(SecurityConstants.BEARER_PREFIX) == true) {
            val token = header.removePrefix(SecurityConstants.BEARER_PREFIX).trim()
            // 构造未认证的 token 放入上下文
            val authentication = SingleTokenAuthenticationToken(token, null)
            SecurityContextHolder.getContext().authentication = authentication
        }
        chain.doFilter(request, response)
    }
}