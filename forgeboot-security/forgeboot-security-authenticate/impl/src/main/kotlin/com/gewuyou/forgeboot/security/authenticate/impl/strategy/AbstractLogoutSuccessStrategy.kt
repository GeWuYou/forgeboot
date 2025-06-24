package com.gewuyou.forgeboot.security.authenticate.impl.strategy

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.security.authenticate.api.strategy.LogoutSuccessStrategy
import com.gewuyou.forgeboot.security.authenticate.impl.constants.ForgeBootSecurityAuthenticationResponseInformation
import com.gewuyou.forgeboot.webmvc.dto.R
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication

/**
 * 抽象注销成功策略
 *
 * 该抽象类为注销成功时的响应处理提供了统一的策略框架。
 * 子类应继承此类并根据需要定制具体注销成功逻辑。
 *
 * @property objectMapper 用于将响应结果序列化为 JSON 格式
 * @constructor 接收一个 ObjectMapper 实例作为参数
 *
 * @since 2025-06-14 20:29:25
 * @author gewuyou
 */
abstract class AbstractLogoutSuccessStrategy(
    private val objectMapper: ObjectMapper,
): LogoutSuccessStrategy {
    /**
     * 注销成功回调方法
     *
     * 当用户成功注销时，该方法会被调用以处理响应。默认实现会返回一个包含成功状态码和消息的 JSON 响应。
     *
     * @param request 表示 HTTP 请求对象，提供有关客户端请求的信息
     * @param response 表示 HTTP 响应对象，用于向客户端发送响应
     * @param authentication 包含认证信息的对象，在注销时提供额外上下文数据
     */
    override fun onLogoutSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        // 创建表示注销成功响应数据对象
        val result = R.success<String>(
            HttpStatus.OK.value(),
            ForgeBootSecurityAuthenticationResponseInformation.LOGOUT_SUCCESS
        )

        // 设置响应状态码为 200 OK
        response.status = HttpStatus.OK.value()

        // 设置响应内容类型为 application/json
        response.contentType = MediaType.APPLICATION_JSON_VALUE

        // 将响应结果序列化为 JSON 并写入响应输出流
        response.writer.write(objectMapper.writeValueAsString(result))
        response.writer.flush()
    }

}