package com.gewuyou.forgeboot.trace.extension

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpMethod
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.reactive.function.client.ClientRequest

/**
 * 请求扩展
 *
 * @since 2025-05-02 21:59:26
 * @author gewuyou
 */

/**
 * 判断是否跳过请求
 * @apiNote 这个方法是给反应式请求id过滤器使用的
 * @return true: 跳过请求; false: 不跳过请求
 */
fun ServerHttpRequest.isSkipRequest(ignorePaths: Array<String>): Boolean {
    return isSkipRequest(this.method.name(), this.uri.path, ignorePaths)
}

/**
 * 判断是否跳过请求
 * @apiNote 这个方法是给请求id过滤器使用的
 * @return true: 跳过请求; false: 不跳过请求
 */
fun HttpServletRequest.isSkipRequest(ignorePaths: Array<String>): Boolean {
    return isSkipRequest(this.method, this.requestURI, ignorePaths)
}

/**
 * 判断是否跳过请求
 * @apiNote 这个方法是给请求id过滤器使用的
 * @return true: 跳过请求; false: 不跳过请求
 */
fun ClientRequest.isSkipRequest(ignorePaths: Array<String>): Boolean {
    return isSkipRequest(this.method().name(), this.url().path, ignorePaths)
}

/**
 * 判断是否跳过请求
 * @param method 请求方法
 * @param uri 请求路径
 * @return true: 跳过请求; false: 不跳过请求
 */
fun isSkipRequest(method: String, uri: String, ignorePaths: Array<String>): Boolean {
    return when {
        // 跳过 OPTIONS 请求
        HttpMethod.OPTIONS.name() == method -> true
        // 跳过 HEAD 请求
        HttpMethod.HEAD.name() == method -> true
        // 跳过 TRACE 请求
        HttpMethod.TRACE.name() == method -> true
        // 跳过模式匹配
        ignorePaths.any { uri.matches(Regex(it)) } -> true
        else -> false
    }
}
