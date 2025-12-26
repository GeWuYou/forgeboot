package com.gewuyou.forgeboot.webmvc.exception.api.enums

/**
 * 异常日志记录策略枚举
 * 定义了异常发生时的日志记录级别和详细程度
 *
 * @since 2025-12-26 15:15:02
 * @author gewuyou
 */
enum class ExceptionLogPolicy {
    /**
     * 不记录日志
     * 异常发生时不进行任何日志记录
     */
    NONE,

    /**
     * 只记录一行：类 / 方法 / 行号
     * 记录异常发生的位置信息，包括类名、方法名和行号
     */
    BRIEF_LOCATION,

    /**
     * 记录完整堆栈
     * 记录完整的异常堆栈跟踪信息，包含详细的调用链路
     */
    FULL_STACK
}
