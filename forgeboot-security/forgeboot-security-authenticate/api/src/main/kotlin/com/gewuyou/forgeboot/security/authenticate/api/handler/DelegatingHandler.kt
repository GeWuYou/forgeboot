package com.gewuyou.forgeboot.security.authenticate.api.handler

import jakarta.servlet.http.HttpServletRequest

/**
 * 委派处理程序接口，用于定义委派处理器的行为规范。
 *
 * 该接口设计为泛型函数式接口，要求实现类提供一个方法，
 * 将给定的 HTTP 请求解析为指定类型的委派对象。
 *
 * @param <T> 委派对象的类型，必须是非空类型
 *
 * @since 2025-06-11 14:51:48
 * @author gewuyou
 */
fun interface DelegatingHandler<T: Any> {
    /**
     * 根据提供的 HTTP 请求解析出对应的委派对象。
     *
     * 此方法通常用于从请求中提取认证信息或上下文数据，
     * 并将其转换为具体的业务对象以供后续处理使用。
     *
     * @param request 要解析的 HTTP 请求对象，不能为 null
     * @return 返回解析得到的委派对象，具体类型由接口泛型 T 决定
     */
    fun resolveDelegate(request: HttpServletRequest): T
}