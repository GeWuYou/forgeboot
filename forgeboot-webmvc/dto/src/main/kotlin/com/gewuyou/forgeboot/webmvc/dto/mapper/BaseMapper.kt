package com.gewuyou.forgeboot.webmvc.dto.mapper

import org.mapstruct.BeanMapping
import org.mapstruct.MappingTarget
import org.mapstruct.NullValuePropertyMappingStrategy

/**
 *Base Mapper （基础映射器）
 *
 * @since 2025-05-30 22:50:18
 * @author gewuyou
 */
interface BaseMapper<T,S> {
    /**
     * 合并 source 到 target，忽略 null 值
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    fun mergeIgnoreNull(@MappingTarget target: T, source: S)

    /**
     * 全量覆盖合并（source 字段即使为 null 也覆盖 target）
     */
    fun overwriteMerge(@MappingTarget target: T, source: S)

    /**
     * 拷贝 source 到新对象
     */
    fun copy(source: S): T
}