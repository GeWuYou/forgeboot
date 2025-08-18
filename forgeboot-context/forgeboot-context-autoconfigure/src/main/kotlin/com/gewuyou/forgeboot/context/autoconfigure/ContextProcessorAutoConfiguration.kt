package com.gewuyou.forgeboot.context.autoconfigure

import com.gewuyou.forgeboot.context.api.FieldRegistry
import com.gewuyou.forgeboot.context.impl.processor.GeneratorProcessor
import com.gewuyou.forgeboot.context.impl.processor.HeaderProcessor
import com.gewuyou.forgeboot.context.impl.processor.MdcProcessor
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean

/**
 * 上下文处理器自动配置
 *
 * 该配置类定义了多个上下文处理相关的 Bean，用于支持线程上下文字段的提取、生成和日志集成。
 *
 * @since 2025-06-24 22:12:58
 * @author gewuyou
 */
@AutoConfiguration
class ContextProcessorAutoConfiguration {

    /**
     * 创建 HeaderProcessor Bean，用于处理请求头中的上下文字段。
     *
     * HeaderProcessor 负责从请求头中提取上下文字段并注入到当前线程上下文中。
     *
     * @param reg 字段注册表，用于获取需要处理的上下文字段定义
     * @return 构建完成的 HeaderProcessor 实例
     */
    @Bean("headerProcessor")
    fun headerProcessor(reg: FieldRegistry): HeaderProcessor {
        return HeaderProcessor(reg)
    }

    /**
     * 创建 GeneratorProcessor Bean，用于生成上下文字段值。
     *
     * GeneratorProcessor 根据字段定义生成默认值（如 traceId、spanId 等），适用于首次进入系统的情况。
     *
     * @param reg 字段注册表，用于获取需要生成值的上下文字段定义
     * @return 构建完成的 GeneratorProcessor 实例
     */
    @Bean("generatorProcessor")
    fun generatorProcessor(reg: FieldRegistry): GeneratorProcessor {
        return GeneratorProcessor(reg)
    }

    /**
     * 创建 MdcProcessor Bean，用于将上下文字段写入 MDC（Mapped Diagnostic Context）。
     *
     * MdcProcessor 使得日志框架（如 Logback）可以访问当前上下文字段，便于日志追踪。
     *
     * @param reg 字段注册表，用于获取需要写入 MDC 的上下文字段定义
     * @return 构建完成的 MdcProcessor 实例
     */
    @Bean("mdcProcessor")
    fun mdcProcessor(reg: FieldRegistry): MdcProcessor {
        return MdcProcessor(reg)
    }
}