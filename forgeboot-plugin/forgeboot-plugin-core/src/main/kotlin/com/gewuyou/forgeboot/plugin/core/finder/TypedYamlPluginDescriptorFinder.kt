package com.gewuyou.forgeboot.plugin.core.finder

import org.pf4j.PluginDescriptor
import org.pf4j.PluginDescriptorFinder
import org.pf4j.PluginRuntimeException
import org.pf4j.util.FileUtils
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * 强类型泛型YAML插件描述符查找器
 * 用于从指定路径中查找并解析YAML格式的插件描述符文件
 *
 * @param descriptorClass 插件描述符的类类型，用于解析YAML文件时构造对应的对象
 * @param yamlFileName    YAML文件的名称，默认为"plugin.yml"
 * @since 2025-07-22 22:26:20
 * @author gewuyou
 */
open class TypedYamlPluginDescriptorFinder<T : PluginDescriptor>(
    protected val descriptorClass: Class<T>,
    protected val yamlFileName: String = "plugin.yml",
) : PluginDescriptorFinder {

    /**
     * 检查给定的插件路径是否适用于此查找器
     *
     * @param pluginPath 插件的路径
     * @return 如果路径存在且为目录或ZIP/JAR文件，则返回true，否则返回false
     */
    override fun isApplicable(pluginPath: Path): Boolean {
        return Files.exists(pluginPath) && (Files.isDirectory(pluginPath) || FileUtils.isZipOrJarFile(pluginPath))
    }

    /**
     * 在给定的插件路径中查找并解析插件描述符
     *
     * @param pluginPath 插件的路径
     * @return 解析后的PluginDescriptor对象
     * @throws PluginRuntimeException 如果无法找到描述符文件，则抛出此异常
     */
    override fun find(pluginPath: Path): PluginDescriptor {
        val path = getYamlPath(pluginPath)
        if (!Files.exists(path)) {
            throw PluginRuntimeException("Cannot find descriptor file: $path")
        }
        return parseYamlDescriptor(path)
    }

    /**
     * 获取YAML描述符文件的路径
     *
     * @param pluginPath 插件的路径
     * @return 描述符文件的路径
     * @throws PluginRuntimeException 如果路径不存在，则抛出此异常
     */
    private fun getYamlPath(pluginPath: Path): Path {
        if (Files.isDirectory(pluginPath)) {
            return pluginPath.resolve(Paths.get(yamlFileName))
        }
        // 处理ZIP或JAR文件的情况，获取其中的YAML文件路径
        try {
            return FileUtils.getPath(pluginPath, yamlFileName)
        } catch (e: IOException) {
            throw PluginRuntimeException(e)
        }
    }

    /**
     * 解析YAML格式的插件描述符
     *
     * @param path 插件描述符的路径
     * @return 解析后的PluginDescriptor对象
     */
    protected open fun parseYamlDescriptor(path: Path): PluginDescriptor {
        return loadYamlFromPath(path)
    }

    /**
     * 从指定路径加载YAML文件并解析为指定的类型
     *
     * @param path YAML文件的路径
     * @return 解析后的对象，类型为T
     * @throws PluginRuntimeException 如果加载或解析YAML失败，则抛出此异常
     */
    protected fun loadYamlFromPath(path: Path): T {
        val inputStream = Files.newInputStream(path)
        val reader = InputStreamReader(inputStream, Charsets.UTF_8)
        val yaml = Yaml(Constructor(descriptorClass, LoaderOptions()))
        return yaml.load<T>(reader)
            ?: throw PluginRuntimeException("Failed to load YAML from: $path")
    }
}
