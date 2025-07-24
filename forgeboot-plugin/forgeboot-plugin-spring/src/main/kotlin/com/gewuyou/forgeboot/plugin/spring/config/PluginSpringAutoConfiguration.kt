package com.gewuyou.forgeboot.plugin.spring.config

import com.gewuyou.forgeboot.plugin.spring.manager.SpringPluginManager
import org.pf4j.DefaultPluginManager.PLUGINS_DIR_CONFIG_PROPERTY_NAME
import org.pf4j.ExtensionFactory
import org.pf4j.PluginManager
import org.pf4j.spring.SingletonSpringExtensionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.Paths

/**
 * 插件Spring自动配置类
 *
 * 该类用于在Spring环境中自动配置插件管理器及相关功能。
 * 提供了插件管理器的Bean定义以及插件集成的初始化逻辑。
 *
 * @since 2025-07-23 13:01:27
 * @author gewuyou
 */
@Configuration
class PluginSpringAutoConfiguration() {
    /**
     * 提供一个默认的插件管理器Bean。
     *
     * 当Spring容器中尚未定义PluginManager类型的Bean时，
     * 该方法将创建并配置一个SpringPluginManager实例作为默认插件管理器。
     * 插件路径通过@Value注解从配置文件中解析，若未配置则使用默认值。
     *
     * @param pluginPath 插件存放路径，从配置文件中读取，默认值为"plugins"
     * @return 返回一个配置好的PluginManager实例，用于管理插件的生命周期和功能
     */
    @Bean
    @ConditionalOnMissingBean
    fun pluginManager(@Value("\${forgeboot.plugin.path}") pluginPath: String = PLUGINS_DIR_CONFIG_PROPERTY_NAME): PluginManager {
        return SpringPluginManager(listOf(Paths.get(pluginPath)))
    }
    /**
     * 创建并配置ExtensionFactory实例
     * 该方法在插件管理器初始化之后调用，用于创建扩展工厂，进一步扩展系统功能
     *
     * @param pluginManager 已初始化并启动的插件管理器，用于扩展工厂的构建
     * @return ExtensionFactory 初始化后的扩展工厂实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun extensionFactory(pluginManager: PluginManager): ExtensionFactory {
        return SingletonSpringExtensionFactory(pluginManager)
    }
}