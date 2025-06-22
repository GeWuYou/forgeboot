package com.gewuyou.forgeboot.context.impl.filter

import com.gewuyou.forgeboot.context.api.ContextProcessor
import com.gewuyou.forgeboot.context.impl.ContextHolder
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

/**
 * 上下文 Servlet 过滤器
 *
 * 该过滤器用于在 HTTP 请求处理过程中提取和注入上下文信息，
 * 确保请求链中各组件可以共享上下文数据。
 *
 * @property chain 处理上下文的处理器链表，按顺序依次执行
 *
 * @since 2025-06-04 16:08:33
 * @author gewuyou
 */
class ContextServletFilter(
    private val chain: List<ContextProcessor>,
    private val contextHolder: ContextHolder
) : OncePerRequestFilter() {

    /**
     * 执行内部过滤逻辑
     *
     * 在请求进入业务逻辑前，从请求中提取上下文信息并存储到上下文持有者中；
     * 在请求完成后，清理上下文以避免内存泄漏或上下文污染。
     *
     * @param request 当前 HTTP 请求对象
     * @param response 当前 HTTP 响应对象
     * @param filterChain 过滤器链，用于继续执行后续过滤器或目标处理逻辑
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        // 创建当前线程上下文快照的可变副本，确保后续操作不影响原始上下文
        val ctx = contextHolder.snapshot().toMutableMap()

        // 遍历上下文处理器链，依次从请求中提取上下文信息并更新临时上下文容器
        chain.forEach { it.extract(request, ctx) }

        // 将提取后的上下文写入当前线程的上下文持有者，供后续组件访问
        ctx.forEach(contextHolder::put)

        // 调用下一个过滤器或最终的目标处理器
        chain.forEach { it.inject(request, ctx) }

        try {
            filterChain.doFilter(request, response)
        } finally {
            // 确保在请求结束时清理所有上下文资源
            // 向处理器链注入空上下文，触发清理操作（如有）
            chain.forEach { it.inject(Unit, mutableMapOf()) }
            // 显式清除当前线程的上下文持有者，防止上下文泄露
            contextHolder.clear()
        }
    }
}