package com.gewuyou.forgeboot.context.autoconfigure

import com.gewuyou.forgeboot.context.api.FieldRegistry
import com.gewuyou.forgeboot.context.impl.ContextHolder
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.ClientRequest

/**
 * Context Web Client 自动配置
 *
 * 配置 WebClient 请求行为，自动将上下文字段注入到 HTTP 请求头中。
 * 适用于 Spring Boot 的 WebClient 请求定制场景。
 *
 * @since 2025-06-24 22:20:10
 * @author gewuyou
 */
@AutoConfiguration
@ConditionalOnClass(name = ["org.springframework.web.reactive.function.client.WebClient"])
class ContextWebClientAutoConfiguration {
    /**
     * 注册 WebClientCustomizer，用于定制 WebClient 的请求行为。
     *
     * 在每次请求发出前，将当前上下文字段写入 HTTP 请求头。
     *
     * @param contextHolder 上下文持有者，用于获取当前线程的上下文快照
     * @param registry      字段注册器，用于获取需传递的上下文字段定义
     * @return 构建完成的 WebClientCustomizer 实例
     */
    @Bean
    fun contextWebClientCustomizer(contextHolder: ContextHolder, registry: FieldRegistry) =
        WebClientCustomizer { builder ->
            // 添加过滤器，在请求发出前处理上下文注入逻辑
            builder.filter { req, next ->
                val ctx = contextHolder.snapshot() // 获取当前上下文快照
                val mutated = ClientRequest.from(req).apply {
                    // 遍历所有注册字段定义，将上下文中的值注入到请求头中
                    registry.all().forEach { def ->
                        ctx[def.key]?.let { value -> header(def.header, value) }
                    }
                }.build()
                next.exchange(mutated) // 执行修改后的请求
            }
        }
}