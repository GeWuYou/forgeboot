package com.gewuyou.forgeboot.security.authenticate.api.resolver

import com.gewuyou.forgeboot.security.core.authenticate.entities.request.LoginRequest
import jakarta.servlet.http.HttpServletRequest

/**
 * 登录请求解析器接口
 * 用于定义处理登录请求的通用规范，包括内容类型支持判断和请求参数解析功能
 *
 * @since 2025-06-10 16:32:32
 * @author gewuyou
 */
interface LoginRequestResolver {

    /**
     * 判断当前解析器是否支持指定的内容类型
     *
     * @param contentType 请求内容类型
     * @return Boolean 返回true表示支持该内容类型，否则不支持
     */
    fun supports(contentType: String): Boolean

    /**
     * 解析HTTP请求为具体的登录请求对象
     * 该方法应从HttpServletRequest中提取并封装登录所需参数
     *
     * @param request HTTP请求对象
     * @return LoginRequest 解析后的登录请求实体对象
     */
    fun resolve(request: HttpServletRequest): LoginRequest
}