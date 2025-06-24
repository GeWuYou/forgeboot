package com.gewuyou.forgeboot.security.authenticate.impl.strategy

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.security.authenticate.api.config.SecurityAuthenticateProperties
import com.gewuyou.forgeboot.security.authenticate.api.strategy.AuthenticationFailureStrategy
import com.gewuyou.forgeboot.webmvc.dto.R
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException

/**
 * 抽象验证失败策略基类
 * 提供统一的认证失败处理机制实现模板
 *
 * @property objectMapper 用于JSON序列化响应结果
 * @property properties 安全认证配置属性
 * @author gewuyou
 * @since 2025-06-14 20:14:14
 */
abstract class AbstractAuthenticationFailureStrategy(
    private val objectMapper: ObjectMapper,
    private val properties: SecurityAuthenticateProperties
) : AuthenticationFailureStrategy {

    /**
     * 处理认证失败的通用实现
     * 将认证失败结果转换为JSON格式的HTTP响应
     *
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param exception 认证异常信息
     */
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException,
    ) {
        // 设置响应内容类型为JSON
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        
        // 获取响应输出流并写入序列化后的失败结果
        val writer = response.writer
        writer.print(
            objectMapper.writeValueAsString(buildFailureResult(exception))
        )
    }

    /**
     * 构建认证失败的响应数据
     * 可被子类重写以提供自定义失败响应
     *
     * @param authenticationException 认证异常信息
     * @return 序列化为JSON的响应数据
     */
    protected open fun buildFailureResult(authenticationException: AuthenticationException): Any {
        // 获取异常消息，若为空则使用默认配置的失败响应
        val message = authenticationException.message
        return R.failure<String>(
            HttpStatus.UNAUTHORIZED.value(),
            message?.let {
                message
            } ?: properties.defaultAuthenticationFailureResponse
        )
    }
}