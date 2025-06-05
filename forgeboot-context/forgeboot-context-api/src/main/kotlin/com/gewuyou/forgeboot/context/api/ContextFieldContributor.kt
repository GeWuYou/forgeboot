package com.gewuyou.forgeboot.context.api

import com.gewuyou.forgeboot.context.api.entities.FieldDef

/**
 * 上下文字段贡献者接口，用于定义上下文所需字段的契约。
 *
 * 实现此接口的类应提供一组上下文字段定义（[FieldDef]），用于描述当前上下文的数据结构。
 *
 * @since 2025-06-04 13:32:39
 * @author gewuyou
 */
fun interface ContextFieldContributor {
    /**
     * 提供上下文字段定义集合。
     *
     * @return 返回一个不可变的[Set]集合，包含当前上下文所需的所有字段定义对象[FieldDef]。
     */
    fun fields(): Set<FieldDef>
}