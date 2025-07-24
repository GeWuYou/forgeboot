package com.gewuyou.forgeboot.plugin.spring

import org.pf4j.PluginWrapper
import org.pf4j.spring.SpringPlugin
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext

/**
 * 隔离的Spring插件
 *
 * 该抽象类继承自SpringPlugin，提供了独立的Spring应用上下文，
 * 使得每个插件可以在自己的上下文中管理Bean，实现插件间的隔离。
 *
 * @property pluginWrapper 插件包装器，提供插件的基本信息和类加载器
 *
 * @since 2025-07-24 15:26:45
 * @author gewuyou
 */
abstract class IsolatedSpringPlugin(
   private val pluginWrapper: PluginWrapper
) : SpringPlugin(pluginWrapper) {

    /**
     * 创建应用上下文
     *
     * 该方法覆盖了父类的实现，创建一个独立的AnnotationConfigApplicationContext，
     * 设置插件的类加载器，并注册插件特定的配置类。
     *
     * @return 配置完成的ApplicationContext实例
     */
    override fun createApplicationContext(): ApplicationContext {
        return AnnotationConfigApplicationContext().apply {
            classLoader = pluginWrapper.pluginClassLoader
            register(pluginConfigurationClass())
            refresh()
        }
    }

    /**
     * 获取插件配置类
     *
     * 抽象方法，子类需要提供插件特定的配置类，
     * 该配置类将被注册到插件的应用上下文中。
     *
     * @return 插件配置类的Class对象
     */
    abstract fun pluginConfigurationClass(): Class<*>
}