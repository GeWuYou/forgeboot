package com.gewuyou.forgeboot.context.impl.processor

import com.gewuyou.forgeboot.context.api.ContextProcessor
import com.gewuyou.forgeboot.context.api.FieldRegistry
import reactor.util.context.Context
import java.util.function.Function
/**
 * 反应器处理器
 *
 * 用于在 WebFlux/Reactor 流中透传字段。当进入异步链时，将字段写入 reactor.util.context.Context。
 * Context 在链尾无需手动清理，会自动复制和管理。
 *
 * @property reg 字段注册表，用于获取所有需要处理的字段定义
 * @since 2025-06-04 15:43:20
 * @author gewuyou
 */
class ReactorProcessor(
    private val reg: FieldRegistry
) : ContextProcessor {
    /**
     * 获取当前处理器的执行顺序优先级。
     *
     * 默认实现返回0，数值越小优先级越高。
     *
     * @return Int 表示当前处理器的顺序值
     */
    override fun order(): Int {
        return 40
    }

    /**
     * 创建一个函数，用于将给定的上下文数据注入到 Reactor 的 Context 中。
     *
     * 遍历字段注册表中的所有字段定义，并将对应的值从输入上下文中取出，
     * 然后放入 Reactor 的 Context 中。
     *
     * @param ctx 包含要注入字段的映射表（key-value）
     * @return Function<Context, Context> 返回一个函数，该函数接受原始的 Reactor Context 并返回更新后的 Context
     */
    fun injectToReactor(ctx: Map<String, String>): Function<Context, Context> =
        Function { rCtx ->
            var updated = rCtx
            // 遍历所有字段定义，并将对应的值注入到 Reactor Context 中
            reg.all().forEach { def ->
                ctx[def.key]?.let { value -> updated = updated.put(def.key, value) }
            }
            updated
        }
}