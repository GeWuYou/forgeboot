package com.gewuyou.forgeboot.plugin.spring

import org.pf4j.PluginWrapper
import org.pf4j.spring.SpringPlugin
import org.pf4j.spring.SpringPluginManager
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.core.env.ConfigurableEnvironment

/**
 * 合并的Spring插件
 *
 * 该抽象类继承自SpringPlugin，与IsolatedSpringPlugin不同，
 * 它创建的插件应用上下文与主应用上下文建立父子关系，
 * 使得插件可以访问主应用上下文中的Bean，实现插件与主应用的合并。
 *
 * @property pluginWrapper 插件包装器，提供插件的基本信息和类加载器
 *
 * @since 2025-07-24 15:35:52
 * @author gewuyou
 */
abstract class MergedSpringPlugin(
   private val pluginWrapper: PluginWrapper
) : SpringPlugin(pluginWrapper) {

    /**
     * 创建应用上下文
     *
     * 该方法覆盖了父类的实现，创建一个与主应用上下文有关联的AnnotationConfigApplicationContext，
     * 设置插件的类加载器，建立父子上下文关系，并注册插件特定的配置类。
     *
     * @return 配置完成的ApplicationContext实例
     */
    override fun createApplicationContext(): ApplicationContext {
        return AnnotationConfigApplicationContext().apply {
            classLoader = pluginWrapper.pluginClassLoader
            parent = (pluginWrapper.pluginManager as SpringPluginManager).applicationContext
            // 保证配置文件能读取
            environment = parent?.environment as ConfigurableEnvironment
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