package com.gewuyou.forgeboot.plugin.demo.impl

import com.gewuyou.forgeboot.plugin.demo.api.GreetingProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 *插件配置
 *
 * @since 2025-07-24 14:15:45
 * @author gewuyou
 */
@Configuration
class PluginConfig{
    @Bean
    fun simpleGreetingService(
        greetingProvider: GreetingProvider
    ): SimpleGreetingService {
        return SimpleGreetingService(greetingProvider)
    }
}