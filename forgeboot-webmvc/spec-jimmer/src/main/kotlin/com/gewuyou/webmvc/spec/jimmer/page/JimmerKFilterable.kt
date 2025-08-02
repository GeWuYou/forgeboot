package com.gewuyou.webmvc.spec.jimmer.page

import com.gewuyou.webmvc.spec.core.page.Filterable
import org.babyfish.jimmer.sql.kt.ast.query.specification.KSpecification
import kotlin.reflect.KClass

/**
 *Jimmer可过滤接口
 *
 * @since 2025-07-30 21:35:21
 * @author gewuyou
 */
interface JimmerKFilterable<EntityType : Any> : Filterable<KSpecification<EntityType>> {
    /**
     * 获取当前的查询规范
     *
     * @return KSpecification<EntityType>? 返回当前的查询规范，可能为null
     */
    fun getSpecification(): KSpecification<EntityType>? = filter

    /**
     * 获取实体类型对应的KClass对象
     *
     * @return KClass<EntityType> 返回实体类型的KClass对象
     */
    fun entityClass(): KClass<EntityType>
}