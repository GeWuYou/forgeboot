package com.gewuyou.forgeboot.context.impl.processor

import com.gewuyou.forgeboot.context.api.ContextProcessor
import com.gewuyou.forgeboot.context.api.FieldRegistry

/**
 * 生成器处理器
 *
 * 用于通过字段注册表（FieldRegistry）动态填充上下文中的空白字段。
 * 当前类实现了 ContextProcessor 接口，提供了一种自动化的上下文字段填充机制。
 *
 * @since 2025-06-04 15:35:11
 * @author gewuyou
 */
class GeneratorProcessor(
    private val reg: FieldRegistry
) : ContextProcessor {
    /**
     * 获取当前处理器的执行顺序优先级。
     *
     * 在多个 ContextProcessor 实现中，该方法决定本处理器的执行顺序。
     * 数值越小，优先级越高，在上下文处理流程中就越早被调用。
     *
     * @return Int 表示当前处理器的顺序值，默认为20
     */
    override fun order(): Int {
        return 20
    }

    /**
     * 从给定的载体中提取上下文信息，并填充到上下文对象中。
     *
     * 遍历 FieldRegistry 中注册的所有字段定义：
     * - 如果当前字段在上下文中不存在或为空白，则尝试使用其关联的生成器函数进行填充。
     * - 生成器函数非空时会被调用，并将结果存入上下文映射中。
     *
     * @param carrier 载体对象，通常包含上下文数据（未使用于当前实现）
     * @param ctx 可变映射，用于存储提取出的上下文键值对
     */
    override fun extract(carrier: Any, ctx: MutableMap<String, String>) {
        reg.all().forEach { def ->
            if (ctx[def.key].isNullOrBlank()) {
                def.generator?.invoke()?.let { ctx[def.key] = it }
            }
        }
    }
}