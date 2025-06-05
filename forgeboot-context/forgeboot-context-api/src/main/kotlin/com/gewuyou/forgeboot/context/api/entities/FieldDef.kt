package com.gewuyou.forgeboot.context.api.entities

import com.gewuyou.forgeboot.context.api.enums.Scope

/**
 * 字段定义，用于描述数据结构中的字段属性。
 *
 * @property header 字段的显示名称，通常用于界面展示。
 * @property key 字段的唯一标识符，用于数据映射和识别。
 * @property generator 生成字段值的函数，默认为 null，表示不使用动态生成。
 * @property scopes 定义该字段适用的上下文范围，默认包括 HEADER 和 MDC。
 *
 * @since 2025-06-04 13:31:32
 * @author gewuyou
 */
data class FieldDef(
    val header: String,
    val key: String,
    val generator: (() -> String)? = null,
    val scopes: Set<Scope> = setOf(Scope.HEADER, Scope.MDC)
)