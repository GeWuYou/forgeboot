package com.gewuyou.forgeboot.context.autoconfigure

import com.gewuyou.forgeboot.context.api.FieldRegistry
import com.gewuyou.forgeboot.context.impl.processor.ReactorProcessor
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean

/**
 * Context Reactor 自动配置
 *
 * 该配置类用于在 Spring Boot 环境中自动装配 ReactorProcessor Bean。
 * 只有在 classpath 中存在 reactor.util.context.Context 类时才会生效。
 *
 * @since 2025-06-24 22:14:18
 * @author gewuyou
 */
@AutoConfiguration
@ConditionalOnClass(name = ["reactor.util.context.Context"])
class ContextReactorAutoConfiguration {

    /**
     * 创建并配置 ReactorProcessor Bean
     *
     * 该方法定义了一个名为 "reactorProcessor" 的 Spring Bean，
     * 用于在 Reactor 响应式流的上下文中传播和管理自定义上下文字段。
     * 
     * @param reg FieldRegistry 实例，用于注册和管理上下文字段
     * @return 配置完成的 ReactorProcessor 实例
     */
    @Bean("reactorProcessor")
    fun reactorProcessor(reg: FieldRegistry) = ReactorProcessor(reg)
}