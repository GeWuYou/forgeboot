package com.gewuyou.forgeboot.context.api

import com.gewuyou.forgeboot.context.api.entities.FieldDef

/**
 * 字段注册表接口
 *
 * 该接口定义了字段注册表的基本操作，包括获取所有字段定义和通过字段头查询字段定义。
 *
 * @since 2025-06-04 14:44:40
 * @author gewuyou
 */
interface FieldRegistry {

    /**
     * 获取所有字段定义的集合
     *
     * @return 返回包含所有字段定义的集合
     */
    fun all(): Collection<FieldDef>

    /**
     * 根据字段头查找对应的字段定义
     *
     * @param header 字段头信息
     * @return 如果找到匹配的字段定义则返回，否则返回 null
     */
    fun findByHeader(header: String): FieldDef?
}