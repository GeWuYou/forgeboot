package com.gewuyou.forgeboot.webmvc.dto.mapper

import org.mapstruct.BeanMapping
import org.mapstruct.MappingTarget
import org.mapstruct.NullValuePropertyMappingStrategy

/**
 * 转换映射器接口
 *
 * 提供通用的实体类(Entity)与数据传输对象(DTO)之间的双向转换能力。
 * 该接口定义了基础的数据转换方法，包括单个对象和集合对象的转换，
 * 并支持部分更新操作（忽略空值属性）。
 *
 * @param <Entity> 实体类类型
 * @param <Dto> 数据传输对象类型
 *
 * @since 2025-05-30 22:53:35
 * @author gewuyou
 */
interface ConversionMapper<Entity, Dto> {
    /**
     * 将实体对象转换为对应的DTO对象
     *
     * @param entity 需要转换的实体对象
     * @return 转换后的DTO对象
     */
    fun toDto(entity: Entity): Dto

    /**
     * 将DTO对象转换为对应的实体对象
     *
     * @param dto 需要转换的DTO对象
     * @return 转换后的实体对象
     */
    fun toEntity(dto: Dto): Entity

    /**
     * 将实体对象列表转换为对应的DTO对象列表
     *
     * @param entityList 需要转换的实体对象列表
     * @return 转换后的DTO对象列表
     */
    fun toDtoList(entityList: List<Entity>): List<Dto>

    /**
     * 将DTO对象列表转换为对应的实体对象列表
     *
     * @param dtoList 需要转换的DTO对象列表
     * @return 转换后的实体对象列表
     */
    fun toEntityList(dtoList: List<Dto>): List<Entity>

    /**
     * 使用非空属性对实体进行部分更新
     *
     * 注意：此操作不会覆盖实体中为空的属性
     *
     * @param dto 需要用于更新的DTO对象
     * @param entity 需要被更新的实体对象
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    fun partialUpdate(dto: Dto, @MappingTarget entity: Entity)
}
