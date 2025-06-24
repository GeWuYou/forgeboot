package com.gewuyou.forgeboot.security.authorize.impl.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.security.authorize.api.config.SecurityAuthorizeProperties
import com.gewuyou.forgeboot.webmvc.dto.R
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * 反应式授权异常处理程序
 *
 * 处理访问被拒绝的授权异常，生成相应的错误响应。
 *
 * @property objectMapper 用于将响应对象序列化为 JSON 字符串
 * @property securityAuthorizeProperties 安全授权配置属性
 * @since 2025-06-23 22:03:09
 * @author gewuyou
 */
class ReactiveAuthorizationExceptionHandler(
    private val objectMapper: ObjectMapper,
    private val securityAuthorizeProperties: SecurityAuthorizeProperties,
) : ServerAccessDeniedHandler {

    /**
     * 处理访问被拒绝的异常，并返回自定义错误响应。
     *
     * 记录异常日志，设置响应状态码和内容类型，并将错误信息以 JSON 格式写入响应。
     *
     * @param exchange 当前的服务器网络交换对象
     * @param denied 抛出的访问被拒绝异常
     * @return 返回一个 Mono<Void> 表示处理完成
     */
    override fun handle(exchange: ServerWebExchange, denied: AccessDeniedException): Mono<Void> {
        // 记录访问被拒绝的异常信息及请求路径
        log.error("反应式授权异常 路径：${exchange.request.uri}", denied)

        // 设置响应状态码为 200 OK
        exchange.response.statusCode = HttpStatus.OK

        // 设置响应内容类型为 application/json
        exchange.response.headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

        // 将错误响应体写入响应
        return exchange.response.writeWith(
            Mono.just(
                exchange.response.bufferFactory().wrap(
                    objectMapper.writeValueAsBytes(
                        R.failure<String>(
                            HttpStatus.FORBIDDEN.value(),
                            denied.message ?: securityAuthorizeProperties.defaultExceptionResponse
                        )
                    )
                )
            )
        )
    }
}