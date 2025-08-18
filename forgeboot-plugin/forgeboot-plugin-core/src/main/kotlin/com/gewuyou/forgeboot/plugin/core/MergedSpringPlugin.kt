package com.gewuyou.forgeboot.plugin.core

import org.pf4j.PluginWrapper
import org.pf4j.spring.SpringPluginManager
import org.springframework.context.ApplicationContext
import org.springframework.core.env.ConfigurableEnvironment

/**
 * 合并的Spring插件
 *
 * 该抽象类继承自AbstractSpringPlugin，与IsolatedSpringPlugin不同，
 * 它创建的插件应用上下文与主应用上下文建立父子关系，
 * 使得插件可以访问主应用上下文中的Bean，实现插件与主应用的合并。
 *
 * @property pluginWrapper 插件包装器，提供插件的基本信息和类加载器
 *
 * @since 2025-07-24 15:35:52
 */
abstract class MergedSpringPlugin(
    private val pluginWrapper: PluginWrapper,
) : AbstractSpringPlugin(pluginWrapper) {
    /**
     * 创建应用上下文
     *
     * 创建一个与主应用上下文有关联的 AnnotationConfigApplicationContext，
     * 设置类加载器、父上下文、环境，并注册插件配置类。
     *
     * @return 配置完成的插件应用上下文，与主应用上下文建立了父子关系
     */
    override fun createApplicationContext(): ApplicationContext {
        // 获取Spring插件管理器
        val manager = pluginWrapper.pluginManager as SpringPluginManager
        // 获取父级应用上下文
        val parentCtx = manager.applicationContext
        // 获取环境配置
        val env = parentCtx.environment as ConfigurableEnvironment
        // 构建插件应用上下文
        return buildSpringContext(pluginWrapper, parentCtx, env)
    }
}