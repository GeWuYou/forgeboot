package com.gewuyou.forgeboot.plugin.demo.impl

import com.gewuyou.forgeboot.plugin.spring.manager.SpringPluginManager
import org.pf4j.PluginWrapper
import org.pf4j.spring.SpringPlugin
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext

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
) : SpringPlugin(pluginWrapper) {
    override fun createApplicationContext(): ApplicationContext {
        return AnnotationConfigApplicationContext().apply { ->
            classLoader = pluginWrapper.pluginClassLoader
            parent = (pluginWrapper.pluginManager as SpringPluginManager).applicationContext
            register(PluginConfig::class.java)
            refresh()
        }
    }
}