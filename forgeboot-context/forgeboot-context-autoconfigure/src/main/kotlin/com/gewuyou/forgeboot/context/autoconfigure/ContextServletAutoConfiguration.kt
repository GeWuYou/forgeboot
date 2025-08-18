package com.gewuyou.forgeboot.context.autoconfigure

import com.gewuyou.forgeboot.context.api.ContextProcessor
import com.gewuyou.forgeboot.context.impl.ContextHolder
import com.gewuyou.forgeboot.context.impl.filter.ContextServletFilter
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

/**
 * 上下文 Servlet 自动配置类
 *
 * 该配置类用于在基于 Servlet 的 Web 应用中自动装配上下文传播相关的组件。
 * 主要功能包括：
 * - 在满足 Servlet 环境条件时启用配置
 * - 注册 ContextServletFilter Bean 以支持请求链中的上下文一致性维护
 *
 * @since 2025-06-24 22:15:23
 * @author gewuyou
 */
@AutoConfiguration
@ConditionalOnProperty(name = ["spring.main.web-application-type"], havingValue = "servlet", matchIfMissing = true)
@ConditionalOnClass(name = ["jakarta.servlet.Filter"])
class ContextServletAutoConfiguration {

    /**
     * 构建并注册 ContextServletFilter 实例
     *
     * 此方法创建一个用于同步阻塞的 Servlet 请求链中的上下文传播过滤器。
     * 只有在容器中尚未存在相同类型的 Bean 时才会注册。
     *
     * @param chain 处理器链，包含多个 ContextProcessor 实例
     * @param contextHolder 上下文持有者，用于存储和传递上下文数据
     * @return 构建完成的 ContextServletFilter 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @Order(Ordered.HIGHEST_PRECEDENCE + 10)
    fun contextServletFilter(chain: List<ContextProcessor>, contextHolder: ContextHolder) =
        ContextServletFilter(chain, contextHolder)
}