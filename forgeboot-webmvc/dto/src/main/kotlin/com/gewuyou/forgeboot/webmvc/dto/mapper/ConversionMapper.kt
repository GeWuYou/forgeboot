package com.gewuyou.forgeboot.webmvc.dto.dto.mapper

/**
 * 转换 映射器
 *
 * 定义了一个转换映射器接口，用于在实体类和数据传输对象（DTO）之间进行转换。
 * 这个接口定义了四个基本转换方法：实体到DTO、DTO到实体、实体列表到DTO列表和DTO列表到实体列表。
 *
 * @param <Entity> 实体类类型
 * @param <Dto> 数据传输对象类型
 *
 * @since 2025-05-30 22:53:35
 * @author gewuyou
 */
interface ConversionMapper<Entity, Dto>{
    /**
     * 将实体对象转换为DTO对象
     *
     * @param entity 实体对象
     * @return 转换后的DTO对象
     */
    fun toDto(entity: Entity): Dto

    /**
     * 将DTO对象转换为实体对象
     *
     * @param dto DTO对象
     * @return 转换后的实体对象
     */
    fun toEntity(dto: Dto): Entity

    /**
     * 将实体对象列表转换为DTO对象列表
     *
     * @param entityList 实体对象列表
     * @return 转换后的DTO对象列表
     */
    fun toDtoList(entityList: List<Entity>): List<Dto>

    /**
     * 将DTO对象列表转换为实体对象列表
     *
     * @param dtoList DTO对象列表
     * @return 转换后的实体对象列表
     */
    fun toEntityList(dtoList: List<Dto>): List<Entity>
}
