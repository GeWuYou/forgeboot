package com.gewuyou.forgeboot.security.authorize.impl.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.security.authorize.api.config.SecurityAuthorizeProperties
import com.gewuyou.forgeboot.webmvc.dto.R
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler

/**
 * 授权异常处理程序，用于处理访问被拒绝的请求并返回对应的 JSON 响应。
 *
 * @property objectMapper 用于将响应对象序列化为 JSON 字符串
 * @property securityAuthorizeProperties 安全授权相关配置属性
 * @author gewuyou
 * @since 2025-06-24 16:35:48
 */
class AuthorizationExceptionHandler(
    private val objectMapper: ObjectMapper,
    private val securityAuthorizeProperties: SecurityAuthorizeProperties
) : AccessDeniedHandler {

    /**
     * 处理访问被拒绝的异常，并写入 JSON 格式的响应体。
     *
     * @param request 发生异常的 HTTP 请求
     * @param response 需要返回的 HTTP 响应
     * @param accessDeniedException 具体访问被拒绝异常
     */
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        // 记录访问被拒绝的异常日志，包含请求路径和异常信息
        log.error("授权异常：路径=${request.requestURI}", accessDeniedException)

        response.status = HttpStatus.OK.value()
        // 设置响应内容类型为 JSON 格式
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        // 设置字符编码为 UTF-8
        response.characterEncoding = "UTF-8"

        // 构建错误响应 JSON 字符串
        val errorJson = objectMapper.writeValueAsString(
            R.failure<String>(
                HttpStatus.FORBIDDEN.value(),
                accessDeniedException.message ?: securityAuthorizeProperties.defaultExceptionResponse
            )
        )
        // 将错误响应写入 HTTP 响应输出流
        response.writer.write(errorJson)
    }
}