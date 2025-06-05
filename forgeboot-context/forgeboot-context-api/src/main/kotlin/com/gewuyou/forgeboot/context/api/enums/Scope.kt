package com.gewuyou.forgeboot.context.api.enums

/**
 * 范围枚举类，定义了不同上下文信息的存储位置
 *
 * @since 2025-06-04 13:29:57
 * @author gewuyou
 */
enum class Scope {
    /**
     * 存储在请求头（Header）中
     */
    HEADER,

    /**
     * 存储在 MDC（Mapped Diagnostic Context）中，用于日志跟踪
     */
    MDC,

    /**
     * 存储在 Reactor 上下文中
     */
    REACTOR,

    /**
     * 存储在 Baggage 中，用于分布式追踪
     */
    BAGGAGE
}