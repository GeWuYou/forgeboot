package com.gewuyou.forgeboot.plugin.core.mapper

import org.pf4j.PluginDescriptor

/**
 * YAML插件描述符映射器
 *
 * 该函数式接口用于将一种数据结构（R类型）映射为插件描述符对象（T类型，继承自PluginDescriptor）。
 * 主要用于插件系统中对不同格式的描述信息进行统一转换。
 *
 * @param T 插件描述符类型，必须继承自 PluginDescriptor
 * @param R 源数据类型，用于提供映射所需的原始信息
 *
 * @since 2025-07-22 22:45:25
 * @author gewuyou
 */
fun interface YamlPluginDescriptorMapper<T : PluginDescriptor, R> {
    /**
     * 将给定的源数据对象映射为插件描述符实例。
     *
     * @param metadata 提供映射所需数据的源对象
     * @return 返回映射生成的插件描述符对象（T类型）
     */
    fun mapFrom(metadata: R): T
}