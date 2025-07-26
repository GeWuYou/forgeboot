package com.gewuyou.forgeboot.plugin.demo.impl

import com.gewuyou.forgeboot.plugin.spring.MergedSpringPlugin
import org.pf4j.PluginWrapper

/**
 * 简单的问候插件
 *
 * 插件主类，必须继承 SpringPlugin 才能在 PF4J + Spring 框架中被识别。
 *
 * @author gewuyou
 * @since 2025-07-24 12:12:21
 */
class SimpleGreetingPlugin(
    private val pluginWrapper: PluginWrapper,
) : MergedSpringPlugin(pluginWrapper) {
    /**
     * 获取插件配置类
     *
     * 抽象方法，子类需要提供插件特定的配置类，
     * 该配置类将被注册到插件的应用上下文中。
     */
    override fun pluginConfigurationClass(): List<Class<*>> {
        return listOf(PluginConfig::class.java)
    }
}