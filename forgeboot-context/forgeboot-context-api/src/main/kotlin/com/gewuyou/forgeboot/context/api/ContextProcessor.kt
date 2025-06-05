package com.gewuyou.forgeboot.context.api

/**
 * 上下文处理器
 *
 * 用于定义上下文的处理逻辑，包括顺序控制、上下文提取与注入。
 *
 * @since 2025-06-04 15:07:13
 * @author gewuyou
 */
interface ContextProcessor : Comparable<ContextProcessor> {

    /**
     * 获取当前处理器的执行顺序优先级。
     *
     * 默认实现返回0，数值越小优先级越高。
     *
     * @return Int 表示当前处理器的顺序值
     */
    fun order(): Int = 0

    /**
     * 从给定的载体中提取上下文信息，并填充到上下文对象中。
     *
     * 默认实现为空方法，子类可根据需要重写此方法。
     *
     * @param carrier 载体对象，通常包含上下文数据
     * @param ctx 可变映射，用于存储提取出的上下文键值对
     */
    fun extract(carrier: Any, ctx: MutableMap<String, String>) {
        // do nothing
    }

    /**
     * 将上下文信息注入到给定的载体中。
     *
     * 默认实现为空方法，子类可根据需要重写此方法。
     *
     * @param carrier 载体对象，将上下文数据注入其中
     * @param ctx 包含上下文键值对的映射
     */
    fun inject(carrier: Any, ctx: MutableMap<String, String>) {
        // do nothing
    }

    /**
     * 根据处理器的执行顺序进行比较，实现Comparable接口的方法。
     *
     * @param other 待比较的另一个上下文处理器
     * @return Int 当前对象与other对象的顺序差值，用于排序
     */
    override fun compareTo(other: ContextProcessor) = order() - other.order()
}