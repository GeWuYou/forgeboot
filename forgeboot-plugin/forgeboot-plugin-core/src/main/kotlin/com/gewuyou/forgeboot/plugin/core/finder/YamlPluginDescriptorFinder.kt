package com.gewuyou.forgeboot.plugin.core.finder

import org.pf4j.*
import org.pf4j.util.FileUtils
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


/**
 *YAML插件描述符查找器
 *
 * @since 2025-07-24 13:24:18
 * @author gewuyou
 */
class YamlPluginDescriptorFinder(
    private val yamlFileName: String = "plugin.yml",
) : PluginDescriptorFinder {

    class YamlDescriptor {
        var pluginId: String? = null
        var pluginDescription: String? = null
        var pluginClass: String? = null
        var version: String? = null
        var provider: String? = null
        var dependencies: String? = null
        var requires: String = "*" // SemVer format
        var license: String? = null
    }


    override fun isApplicable(pluginPath: Path): Boolean {
        return Files.exists(pluginPath) && (Files.isDirectory(pluginPath) || FileUtils.isZipOrJarFile(pluginPath))
    }

    override fun find(pluginPath: Path): PluginDescriptor {
        val descriptorPath = getYamlPath(pluginPath)
        if (!Files.exists(descriptorPath)) {
            throw PluginRuntimeException("YAML descriptor file not found: $descriptorPath")
        }

        val yaml = Yaml(LoaderOptions())
        val reader = InputStreamReader(Files.newInputStream(descriptorPath), Charsets.UTF_8)
        val yamlDescriptor = yaml.loadAs(reader, YamlDescriptor::class.java)

        return DefaultPluginDescriptor(
            yamlDescriptor.pluginId,
            yamlDescriptor.pluginDescription,
            yamlDescriptor.pluginClass,
            yamlDescriptor.version,
            yamlDescriptor.requires,
            yamlDescriptor.provider,
            yamlDescriptor.license
        ).apply {
            yamlDescriptor.dependencies
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?.forEach { addDependency(PluginDependency(it)) }
        }
    }

    private fun getYamlPath(pluginPath: Path): Path {
        return if (Files.isDirectory(pluginPath)) {
            pluginPath.resolve(Paths.get(yamlFileName))
        } else {
            try {
                FileUtils.getPath(pluginPath, yamlFileName)
            } catch (e: Exception) {
                throw PluginRuntimeException(e)
            }
        }
    }
}
