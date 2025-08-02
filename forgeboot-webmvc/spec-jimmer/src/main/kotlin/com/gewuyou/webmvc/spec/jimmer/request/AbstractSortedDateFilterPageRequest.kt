package com.gewuyou.webmvc.spec.jimmer.request

import com.gewuyou.webmvc.spec.core.entities.SortCondition
import com.gewuyou.webmvc.spec.core.enums.SortDirection
import com.gewuyou.webmvc.spec.core.page.Pageable
import com.gewuyou.webmvc.spec.core.page.Sortable
import com.gewuyou.webmvc.spec.jimmer.page.JimmerKFilterable
import org.babyfish.jimmer.sql.kt.ast.query.specification.KSpecification

/**
 *抽象排序日期过滤分页请求
 *
 * @since 2025-07-30 21:59:34
 * @author gewuyou
 */
abstract class AbstractSortedDateFilterPageRequest<T : Any>(
    override val currentPage: Int = 1,
    override val pageSize: Int = 10,
    override val sortBy: String = "createdAt",
    override val sortDirection: SortDirection = SortDirection.DESC,
    override val sortConditions: List<SortCondition> = emptyList(),
    override val filter: KSpecification<T>? = null,
) : Pageable, Sortable, JimmerKFilterable<T>