package com.gewuyou.forgeboot.plugin.core.finder

import com.gewuyou.forgeboot.plugin.core.mapper.YamlPluginDescriptorMapper
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
 * 映射YAML插件描述符查找器
 *
 * @since 2025-07-22 22:46:42
 * @author gewuyou
 */
class MappableYamlPluginDescriptorFinder<T : PluginDescriptor, R>(
    private val metadataClass: Class<R>,
    private val descriptorMapper: YamlPluginDescriptorMapper<T, R>,
    private val yamlFileName: String = "plugin.yml"
) : PluginDescriptorFinder {

    /**
     * 判断插件路径是否适用当前查找器
     *
     * @param pluginPath 插件路径
     * @return 如果路径存在且为目录或 ZIP/JAR 文件则返回 true
     */
    override fun isApplicable(pluginPath: Path): Boolean {
        return Files.exists(pluginPath) && (Files.isDirectory(pluginPath) || FileUtils.isZipOrJarFile(pluginPath))
    }

    /**
     * 查找并解析插件描述符
     *
     * @param pluginPath 插件路径
     * @return 解析后的插件描述符
     */
    override fun find(pluginPath: Path): PluginDescriptor {
        val path = getYamlPath(pluginPath)
        if (!Files.exists(path)) {
            throw PluginRuntimeException("Cannot find descriptor file: $path")
        }
        val metadata = loadYamlFromPath(path)
        return descriptorMapper.mapFrom(metadata)
    }

    /**
     * 获取 YAML 文件的路径
     *
     * @param pluginPath 插件路径
     * @return YAML 文件的路径
     */
    private fun getYamlPath(pluginPath: Path): Path {
        return if (Files.isDirectory(pluginPath)) {
            pluginPath.resolve(Paths.get(yamlFileName))
        } else {
            try {
                FileUtils.getPath(pluginPath, yamlFileName)
            } catch (e: IOException) {
                throw PluginRuntimeException(e)
            }
        }
    }

    /**
     * 从指定路径加载 YAML 数据
     *
     * @param path YAML 文件路径
     * @return 解析后的对象
     */
    private fun loadYamlFromPath(path: Path): R {
        val inputStream = Files.newInputStream(path)
        val reader = InputStreamReader(inputStream, Charsets.UTF_8)
        val yaml = Yaml(Constructor(metadataClass, LoaderOptions()))
        return yaml.load<R>(reader)
            ?: throw PluginRuntimeException("Failed to load YAML from: $path")
    }
}
