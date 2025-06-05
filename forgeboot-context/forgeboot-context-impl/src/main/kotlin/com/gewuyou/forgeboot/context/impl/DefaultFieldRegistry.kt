package com.gewuyou.forgeboot.context.impl

import com.gewuyou.forgeboot.context.api.FieldRegistry
import com.gewuyou.forgeboot.context.api.entities.FieldDef

/**
 * 默认字段注册表
 *
 * 该类实现了字段的注册和查找功能，通过字段的 key 和 header 建立索引，
 * 提供了基于 header 的快速查找能力。
 *
 * @param defs 初始化字段定义集合，用于构建注册表
 * @since 2025-06-04 14:51:39
 * @author gewuyou
 */
class DefaultFieldRegistry(defs: Set<FieldDef>) : FieldRegistry {
    /**
     * 按字段 key 构建的有序映射表，用于通过 key 快速访问字段定义。
     * 保持字段注册顺序的一致性，适用于需要按注册顺序遍历的场景。
     */
    private val byKey: Map<String, FieldDef> =
        LinkedHashMap<String, FieldDef>().apply {
            defs.forEach { put(it.key, it) }
        }

    /**
     * 按字段 header（小写形式）构建的映射表，用于通过 header 快速查找字段定义。
     */
    private val byHeader = defs.associateBy { it.header.lowercase() }

    /**
     * 获取注册表中所有的字段定义。
     *
     * @return 字段定义的集合
     */
    override fun all() = byKey.values

    /**
     * 根据字段 header 查找对应的字段定义。
     *
     * @param header 要查找的字段 header（不区分大小写）
     * @return 匹配到的字段定义，若未找到则返回 null
     */
    override fun findByHeader(header: String) = byHeader[header.lowercase()]
}