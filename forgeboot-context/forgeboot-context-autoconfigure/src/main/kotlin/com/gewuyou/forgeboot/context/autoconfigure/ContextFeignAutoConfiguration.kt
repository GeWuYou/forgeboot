package com.gewuyou.forgeboot.context.autoconfigure

import com.gewuyou.forgeboot.context.api.FieldRegistry
import com.gewuyou.forgeboot.context.impl.ContextHolder
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 上下文Feign自动配置类
 *
 * 该配置类用于在Spring Boot应用中自动装配与上下文传播相关的Feign拦截器。
 * 当Feign库存在时，该配置会创建一个请求拦截器，用于在Feign调用过程中传播上下文字段。
 *
 * @since 2025-06-24 22:17:46
 * @author gewuyou
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = ["feign.RequestInterceptor"])
class ContextFeignAutoConfiguration {
    /**
     * 创建 Feign 请求拦截器 Bean。
     *
     * 该拦截器会在每次 Feign 请求发起前执行，将当前上下文中的字段值写入 HTTP 请求头，
     * 以支持跨服务调用的上下文传播。
     *
     * @param registry 字段注册表，用于获取需要传播的字段定义
     * @param contextHolder 上下文持有者，用于获取当前线程的上下文快照
     * @return 构建完成的 feign.RequestInterceptor 实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun feignInterceptor(registry: FieldRegistry, contextHolder: ContextHolder) =
        feign.RequestInterceptor { tpl ->
            // 获取当前上下文快照
            val ctx = contextHolder.snapshot()
            // 遍历所有已注册的字段定义
            registry.all().forEach { def ->
                // 如果当前字段有值，则将其添加到 HTTP 请求头中
                ctx[def.key]?.let { tpl.header(def.header, it) }
            }
        }
}