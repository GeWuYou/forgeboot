package com.gewuyou.forgeboot.context.impl.processor

import com.gewuyou.forgeboot.context.api.ContextProcessor
import com.gewuyou.forgeboot.context.api.FieldRegistry
import org.slf4j.MDC

/**
 * MDC 处理器
 * 把 ctx 写入 SLF4J MDC，方便日志打印；请求结束或线程任务结束时清理。
 *
 *
 * @since 2025-06-04 15:39:35
 * @author gewuyou
 */
class MdcProcessor(
    private val reg: FieldRegistry,
) : ContextProcessor {
    /**
     * 获取当前处理器的执行顺序优先级。
     *
     * 默认实现返回0，数值越小优先级越高。
     *
     * @return Int 表示当前处理器的顺序值
     */
    override fun order(): Int {
        return 30
    }

    /**
     * 将上下文信息注入到给定的载体中。
     *
     * 默认实现为空方法，子类可根据需要重写此方法。
     *
     * @param carrier 载体对象，将上下文数据注入其中
     * @param ctx 包含上下文键值对的映射
     */
    override fun inject(carrier: Any, ctx: MutableMap<String, String>) {
        if (ctx.isEmpty()) {
            // 视为空 ctx → 清理
            reg.all().forEach { def -> MDC.remove(def.key) }
        } else {
            // 正常写入
            reg.all().forEach { def ->
                ctx[def.key]?.let { MDC.put(def.key, it) }
            }
        }
    }
}