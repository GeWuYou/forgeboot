package com.gewuyou.forgeboot.security.authenticate.impl.handler

import com.gewuyou.forgeboot.security.authenticate.api.handler.DelegatingHandler
import com.gewuyou.forgeboot.security.authenticate.impl.resolver.CompositeLoginRequestResolver
import jakarta.servlet.http.HttpServletRequest

/**
 * 抽象委派处理程序
 *
 * 该类为泛型抽象类，用于实现委派处理逻辑，通过解析请求获取登录类型，并使用上下文解析器获取对应的处理上下文。
 *
 * @param resolver 用于解析请求并获取登录类型的组件，不可为空
 * @param contextResolver 根据登录类型生成对应上下文对象的函数式接口，不可为空
 *
 * @since 2025-06-11 14:53:46
 * @author gewuyou
 */
abstract class AbstractDelegatingHandler<T : Any>(
    private val resolver: CompositeLoginRequestResolver,
    private val contextResolver: (String) -> T,
): DelegatingHandler<T> {
    /**
     * 解析请求并获取对应的处理上下文
     *
     * 该方法会调用注入的 [resolver] 来解析传入的 HTTP 请求，获取当前请求的登录类型，
     * 然后通过 [contextResolver] 函数将登录类型转换为具体地处理上下文对象。
     *
     * @param request HTTP 请求对象，用于解析登录类型
     * @return 返回与登录类型对应的处理上下文对象
     */
    override fun resolveDelegate(request: HttpServletRequest): T {
        // 解析请求获取登录类型
        val loginType = resolver.resolve(request).getType()
        // 使用上下文解析器生成对应类型的处理上下文
        return contextResolver(loginType)
    }
}