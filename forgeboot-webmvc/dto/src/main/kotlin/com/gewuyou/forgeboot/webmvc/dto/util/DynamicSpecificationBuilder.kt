package com.gewuyou.forgeboot.webmvc.dto.util

import com.gewuyou.forgeboot.webmvc.dto.extension.Equal
import com.gewuyou.forgeboot.webmvc.dto.extension.In
import com.gewuyou.forgeboot.webmvc.dto.extension.Like
import com.gewuyou.forgeboot.webmvc.dto.extension.OrGroup
import com.gewuyou.forgeboot.webmvc.dto.extension.getPredicates
import com.gewuyou.forgeboot.webmvc.dto.request.PageQueryReq
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import kotlin.collections.isNotEmpty
import kotlin.collections.toTypedArray
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * 动态构建 JPA Specification 的工具类
 *
 * 支持字段注解控制查询方式（@Like, @Equal, @In）
 * 支持嵌套字段路径（如 "user.name"）
 * 支持 OR 条件组（@OrGroup("groupName")）
 * @since 2025-05-28 23:37:17
 * @author gewuyou
 */
object DynamicSpecificationBuilder {

    /**
     * 构建动态查询 Specification
     *
     * @param query PageQuery<T> 查询参数对象
     * @param createAtName 时间字段名
     * @param enabledName 是否启用字段名
     * @param deletedName 是否删除字段名
     */
    inline fun <reified T : Any, reified E> build(
        query: PageQueryReq<T>,
        createAtName: String = "createdAt",
        enabledName: String = "enabled",
        deletedName: String = "deleted",
    ): Specification<E> {
        return Specification { root, _, builder ->
            val predicates = mutableListOf<Predicate>()

            // 1. 加入基本条件（时间、启用、删除）
            predicates += query.getPredicates(builder, root, createAtName, enabledName, deletedName)

            // 2. 处理 filter 字段
            val filterObj = query.filter ?: return@Specification builder.and(*predicates.toTypedArray())

            // 3. OR 分组
            val orGroups = mutableMapOf<String, MutableList<Predicate>>()

            T::class.memberProperties.forEach { prop ->
                prop.isAccessible = true
                val value = prop.get(filterObj) ?: return@forEach

                val fieldPath = prop.name
                val path = getPath(root, fieldPath)

                val predicate = when {
                    prop.findAnnotation<Like>() != null && value is String ->
                        builder.like(path as Path<String>, "%$value%")

                    prop.findAnnotation<Equal>() != null ->
                        builder.equal(path, value)

                    prop.findAnnotation<In>() != null && value is Collection<*> ->
                        path.`in`(value)

                    // 默认策略：非空值执行 equal
                    else -> builder.equal(path, value)
                }

                val orGroup = prop.findAnnotation<OrGroup>()?.value
                if (orGroup != null) {
                    orGroups.getOrPut(orGroup) { mutableListOf() }.add(predicate)
                } else {
                    predicates.add(predicate)
                }
            }

            // 4. 添加 OR 条件组
            orGroups.values.forEach { group ->
                if (group.isNotEmpty()) {
                    predicates.add(builder.or(*group.toTypedArray()))
                }
            }
            builder.and(*predicates.toTypedArray())
        }
    }

    /**
     * 支持嵌套字段路径解析，例如 "user.name"
     */
    fun <E> getPath(root: Root<E>, pathStr: String): Path<*> {
        return pathStr.split(".").fold(root as Path<*>) { path, part ->
            path.get<Any>(part)
        }
    }

}