package com.gewuyou.forgeboot.cache.autoconfigure.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.core.serialization.serializer.ValueSerializer
import com.gewuyou.forgeboot.core.serialization.serializer.impl.serializer.JacksonValueSerializer

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 缓存序列化程序配置
 *
 * 该配置类用于定义缓存所需的序列化器 Bean。
 *
 * @since 2025-06-21 11:40:45
 * @author gewuyou
 */
@Configuration
class CacheSerializerConfig {

    /**
     * 创建 JacksonValueSerializer 序列化器 Bean
     *
     * 该方法定义了一个 JacksonValueSerializer 的 Bean，用于将值序列化和反序列化，
     * 适用于与 Jackson 库集成进行 JSON 数据格式的处理。
     *
     * @param objectMapper Jackson 提供的对象映射器，用于实际的序列化/反序列化操作
     * @return 返回一个 JacksonValueSerializer 实例，作为 ValueSerializer 接口的实现
     */
    @Bean
    @ConditionalOnMissingBean
    fun serializer(objectMapper: ObjectMapper): ValueSerializer {
        return JacksonValueSerializer(objectMapper)
    }
}