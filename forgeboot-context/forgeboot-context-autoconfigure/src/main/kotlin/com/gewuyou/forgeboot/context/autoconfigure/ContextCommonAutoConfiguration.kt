package com.gewuyou.forgeboot.context.autoconfigure

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.context.api.ContextFieldContributor
import com.gewuyou.forgeboot.context.api.FieldRegistry
import com.gewuyou.forgeboot.context.impl.ContextHolder
import com.gewuyou.forgeboot.context.impl.DefaultFieldRegistry
import com.gewuyou.forgeboot.core.serialization.serializer.ValueSerializer
import com.gewuyou.forgeboot.core.serialization.serializer.impl.serializer.JacksonValueSerializer
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

/**
 * Context Common 自动配置
 *
 * 该配置类定义了上下文模块的基础 Bean，包括序列化器、上下文持有者、字段注册表以及多种处理器。
 * 所有 Bean 均使用 @ConditionalOnMissingBean 注解以确保仅在容器中没有其他同类型 Bean 时才创建。
 *
 * @since 2025-06-24 22:09:55
 * @author gewuyou
 */
@AutoConfiguration
class ContextCommonAutoConfiguration {

    /**
     * 创建 ValueSerializer Bean，用于在缺少其他实现时提供默认的值序列化器。
     *
     * 该方法定义了一个 JacksonValueSerializer 实例作为默认的 ValueSerializer 实现，
     * 负责使用 Jackson 库对上下文字段值进行序列化和反序列化操作。
     *
     * @param objectMapper 提供 JSON 序列化支持的 ObjectMapper 实例
     * @return 构建完成的 ValueSerializer 实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun valueSerializer(objectMapper: ObjectMapper): ValueSerializer {
        return JacksonValueSerializer(objectMapper)
    }

    /**
     * 创建 ContextHolder Bean，用于在缺少其他实现时提供默认的上下文持有者。
     *
     * 该方法定义了一个 ContextHolder 实例作为默认的上下文管理器，
     * 负责存储、传递和清理当前线程的上下文字段数据。
     *
     * @param valueSerializer 提供值序列化支持的 ValueSerializer 实例
     * @return 构建完成的 ContextHolder 实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun contextHolder(valueSerializer: ValueSerializer): ContextHolder {
        return ContextHolder(valueSerializer)
    }


    /**
     * 创建 FieldRegistry Bean，用于注册上下文中所有字段定义。
     *
     * FieldRegistry 是上下文字段的核心注册中心，聚合所有 ContextFieldContributor 提供的字段定义。
     *
     * @param contributors 提供字段定义的贡献者列表
     * @return 构建完成的 FieldRegistry 实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun fieldRegistry(contributors: List<ContextFieldContributor>): FieldRegistry =
        DefaultFieldRegistry(contributors.flatMap { it.fields() }.toSet())
}