package com.gewuyou.forgeboot.webmvc.dto.mapper

import org.mapstruct.BeanMapping
import org.mapstruct.MappingTarget
import org.mapstruct.NullValuePropertyMappingStrategy

/**
 * Base Mapper（基础映射器）
 * 提供基础的对象映射操作定义，包含合并、覆盖合并、单个对象拷贝及列表拷贝的方法。
 *
 * @since 2025-05-30 22:50:18
 * @author gewuyou
 */
interface BaseMapper<S, T> {
    /**
     * 将 source 对象中的非 null 属性合并到 target 对象中。
     * 注意：null 值的属性不会覆盖 target 中已有的值。
     *
     * @param target 目标对象，将被更新
     * @param source 源对象，提供需要合并的数据
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    fun mergeIgnoreNull(@MappingTarget target: T, source: S)

    /**
     * 全量覆盖合并 source 到 target。
     * 注意：即使 source 中的字段为 null，也会覆盖 target 中对应的字段。
     *
     * @param target 目标对象，将被更新
     * @param source 源对象，提供需要合并的数据
     */
    fun overwriteMerge(@MappingTarget target: T, source: S)

    /**
     * 将 source 对象的内容拷贝到一个新的 T 类型对象中。
     *
     * @param source 源对象，提供数据
     * @return 返回一个新的目标类型对象
     */
    fun copy(source: S): T

    /**
     * 将源对象列表中的每个元素拷贝为新的目标类型对象，生成一个目标对象列表。
     *
     * @param sources 源对象列表
     * @return 返回目标类型对象的列表
     */
    fun copyList(sources: List<S>): List<T>
}