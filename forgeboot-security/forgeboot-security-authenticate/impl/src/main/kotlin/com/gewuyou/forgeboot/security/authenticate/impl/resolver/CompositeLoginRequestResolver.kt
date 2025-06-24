package com.gewuyou.forgeboot.security.authenticate.impl.resolver

import com.gewuyou.forgeboot.security.authenticate.api.resolver.LoginRequestResolver
import com.gewuyou.forgeboot.security.core.authenticate.entities.request.LoginRequest
import jakarta.servlet.http.HttpServletRequest
import kotlin.collections.find

/**
 * 复合登录请求解析器
 *
 * 该类用于根据请求的内容类型(contentType)动态选择合适的登录请求解析器。
 * 它通过组合多种 LoginRequestResolver实现，支持不同格式的登录请求解析。
 *
 * @property resolvers 提供支持的各种登录请求解析器列表
 *
 * @since 2025-06-10 16:46:57
 * @author gewuyou
 */
class CompositeLoginRequestResolver(
    private val resolvers: List<LoginRequestResolver>
) {
    /**
     * 解析登录请求
     *
     * 根据给定的 HTTP 请求中的内容类型(contentType)，查找支持该类型的解析器，
     * 并使用该解析器解析登录请求。
     *
     * @param request 需要解析的 HTTP 请求
     * @return 解析后的登录请求对象(LoginRequest)
     * @throws IllegalArgumentException 如果没有找到支持当前内容类型的解析器
     */
    fun resolve(request: HttpServletRequest): LoginRequest {
        val contentType = request.contentType ?: ""
        val resolver = resolvers.find { it.supports(contentType) }
            ?: throw IllegalArgumentException("Unsupported content type: $contentType")
        return resolver.resolve(request)
    }
}