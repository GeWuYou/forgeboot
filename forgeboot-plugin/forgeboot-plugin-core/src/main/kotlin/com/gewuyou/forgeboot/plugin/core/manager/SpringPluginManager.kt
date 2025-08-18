package com.gewuyou.forgeboot.plugin.core.manager

import com.gewuyou.forgeboot.plugin.core.finder.YamlPluginDescriptorFinder
import org.pf4j.CompoundPluginDescriptorFinder
import org.pf4j.ManifestPluginDescriptorFinder
import org.pf4j.PropertiesPluginDescriptorFinder
import org.pf4j.spring.SpringPluginManager
import java.nio.file.Path

/**
 *Spring插件管理器
 *
 * @since 2025-07-24 13:15:40
 * @author gewuyou
 */
open class SpringPluginManager(
    pluginsRoots: List<Path>,
) : SpringPluginManager(pluginsRoots) {
    override fun createPluginDescriptorFinder(): CompoundPluginDescriptorFinder {
        return CompoundPluginDescriptorFinder()
            .add(YamlPluginDescriptorFinder())
            .add(PropertiesPluginDescriptorFinder())
            .add(ManifestPluginDescriptorFinder())
    }
}