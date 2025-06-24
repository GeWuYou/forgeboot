package com.gewuyou.forgeboot.security.authenticate.impl.strategy

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.security.authenticate.api.strategy.AuthenticationSuccessStrategy
import com.gewuyou.forgeboot.security.authenticate.impl.constants.ForgeBootSecurityAuthenticationResponseInformation
import com.gewuyou.forgeboot.webmvc.dto.R
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication

/**
 * 抽象认证成功策略
 *
 * 该基础类为认证成功后的处理提供统一模板
 *
 * @property objectMapper 用于将响应对象序列化为 JSON 字符串
 * @since 2025-06-12 21:26:11
 * @author gewuyou
 */
abstract class AbstractAuthenticationSuccessStrategy(
    private val objectMapper: ObjectMapper,
) : AuthenticationSuccessStrategy {

    /**
     * 认证成功时触发的方法
     *
     * 处理 HTTP 响应的设置，包括状态码、内容类型和写入响应数据
     *
     * @param request 本次请求的 HttpServletRequest 对象
     * @param response 本次响应的 HttpServletResponse 对象
     * @param authentication 包含认证信息的对象
     */
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        // 构建认证成功响应数据
        val result = buildSuccessResponse(authentication)
        // 设置响应状态码为 200 OK
        response.status = HttpStatus.OK.value()
        // 设置响应内容类型为 application/json
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        // 将响应结果序列化为 JSON 并写入响应输出流
        response.writer.write(objectMapper.writeValueAsString(result))
        response.writer.flush()
    }

    /**
     * 构建认证成功响应体对象
     *
     * @param authentication 包含认证信息的对象
     * @return 返回准备序列化并发送给客户端的响应对象
     */
    protected open fun buildSuccessResponse(authentication: Authentication): Any {
        return R.success(
            HttpStatus.OK.value(),
            ForgeBootSecurityAuthenticationResponseInformation.LOGIN_SUCCESS, authentication.principal
        )
    }
}