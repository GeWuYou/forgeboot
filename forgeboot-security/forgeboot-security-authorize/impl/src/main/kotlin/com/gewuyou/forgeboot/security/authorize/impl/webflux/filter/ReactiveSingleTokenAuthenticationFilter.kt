package com.gewuyou.forgeboot.security.authorize.impl.webflux.filter

import com.gewuyou.forgeboot.security.core.common.constants.SecurityConstants
import com.gewuyou.forgeboot.security.core.common.token.SingleTokenAuthenticationToken
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * 反应式单令牌身份验证过滤器
 *
 * 用于在 WebFlux 环境下通过单 Token 进行身份验证的过滤器。
 * 它继承自 [AuthenticationWebFilter]，并使用 [SingleTokenServerAuthenticationConverter]
 * 将请求中的 Token 转换为认证对象 [SingleTokenAuthenticationToken]。
 *
 * @property authenticationManager 身份验证管理器，用于处理身份验证请求
 * @since 2025-06-25 16:46:13
 * @author gewuyou
 */
class ReactiveSingleTokenAuthenticationFilter(
    authenticationManager: ReactiveAuthenticationManager
) : AuthenticationWebFilter(authenticationManager) {

    /**
     * 初始化代码块，设置当前过滤器使用的身份验证转换器。
     *
     * 此处将默认的转换逻辑替换为基于 Token 的身份验证转换器，
     * 该转换器负责从请求头中提取并解析 Token。
     */
    init {
        setServerAuthenticationConverter(SingleTokenServerAuthenticationConverter())
    }

    /**
     * 基于 Token 的身份验证转换器实现类。
     *
     * 实现了 [ServerAuthenticationConverter] 接口，负责从 [ServerWebExchange] 中提取 Token，
     * 并将其转换为对应的认证对象 [SingleTokenAuthenticationToken]。
     */
    private class SingleTokenServerAuthenticationConverter : ServerAuthenticationConverter {

        /**
         * 将传入的请求上下文转换为认证对象。
         *
         * 此方法尝试从请求头中获取 `Authorization` 字段，并检查其是否以指定的前缀开头。
         * 如果匹配，则移除前缀并提取出 Token，最终构造一个未认证的 [SingleTokenAuthenticationToken] 对象。
         *
         * @param exchange 当前的服务器 Web 交换对象，包含请求信息
         * @return 返回一个 [Mono<Authentication>?] 类型的对象，如果成功提取 Token 则返回包含认证对象的 Mono；
         *         否则返回空的 Mono。
         */
        override fun convert(exchange: ServerWebExchange): Mono<Authentication>? {
            val headerValue = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
            return if (headerValue?.startsWith(SecurityConstants.BEARER_PREFIX, ignoreCase = true) == true) {
                val singleToken = headerValue.removePrefix(SecurityConstants.BEARER_PREFIX).trim()
                Mono.just(SingleTokenAuthenticationToken(singleToken, null))
            } else {
                Mono.empty()
            }
        }
    }
}