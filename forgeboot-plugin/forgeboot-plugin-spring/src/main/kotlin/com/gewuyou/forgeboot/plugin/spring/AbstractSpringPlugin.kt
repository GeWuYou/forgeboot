package com.gewuyou.forgeboot.plugin.spring

import com.gewuyou.forgeboot.plugin.core.exception.PluginInitializationException
import org.pf4j.PluginWrapper
import org.pf4j.spring.SpringPlugin
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.core.env.ConfigurableEnvironment

/**
 *抽象Spring插件
 *
 * @since 2025-07-26 11:27:26
 * @author gewuyou
 */
abstract class AbstractSpringPlugin(
    pluginWrapper: PluginWrapper,
) : SpringPlugin(pluginWrapper) {
    /**
     * 获取插件配置类
     *
     * 抽象方法，子类需要提供插件特定的配置类，
     * 该配置类将被注册到插件的应用上下文中。
     */
    abstract fun pluginConfigurationClass(): List<Class<*>>

    /**
     * 构建Spring应用上下文
     *
     * 该函数用于创建并配置一个AnnotationConfigApplicationContext实例，
     * 包括设置类加载器、父上下文、环境配置，注册配置类并刷新上下文。
     *
     * @param pluginWrapper 插件包装器，提供插件的基本信息和类加载器
     * @param parentContext 父级应用上下文，可为空
     * @param environment 可选的环境配置
     * @return 配置完成的AnnotationConfigApplicationContext实例
     */
    protected fun buildSpringContext(
        pluginWrapper: PluginWrapper,
        parentContext: ApplicationContext? = null,
        environment: ConfigurableEnvironment? = null,
    ): AnnotationConfigApplicationContext {
        try {
            val configClasses = pluginConfigurationClass().also {
                require(it.isNotEmpty()) {
                    "插件 [${pluginWrapper.pluginId}] 未提供任何配置类"
                }
            }

            return AnnotationConfigApplicationContext().apply {
                // 设置插件类加载器
                classLoader = pluginWrapper.pluginClassLoader
                // 设置父级上下文
                parent = parentContext
                // 如果提供了环境配置，则设置环境
                if (environment != null) {
                    this.environment = environment
                }
                // 注册所有配置类
                configClasses.forEach { register(it) }
                // 刷新上下文以完成初始化
                refresh()
                log.info(
                    "✅ 插件 [{}] 上下文加载成功，注册配置类: {}",
                    pluginWrapper.pluginId,
                    configClasses.joinToString()
                )
            }
        } catch (e: Exception) {
            log.error("❌ 插件 [{}] 加载失败", pluginWrapper.pluginId, e)
            throw PluginInitializationException("插件 [${pluginWrapper.pluginId}] 初始化失败", e)
        }
    }

}