package com.gewuyou.webmvc.spec.jimmer.request

import com.gewuyou.webmvc.spec.core.enums.SortDirection
import com.gewuyou.webmvc.spec.core.page.Pageable
import com.gewuyou.webmvc.spec.jimmer.page.JimmerKFilterable
import org.babyfish.jimmer.sql.kt.ast.query.specification.KSpecification

/**
 *抽象过滤分页请求
 *
 * @param T 实体类型参数
 * @param currentPage 当前页码，默认值为1
 * @param pageSize 每页大小，默认值为10
 * @param filter 查询过滤条件，可为空，默认值为null
 * @param sortDirection 排序方向
 * @since 2025-07-30 21:53:03
 * @author gewuyou
 */
abstract class AbstractFilteredPageRequest<T : Any>(
    override val currentPage: Int = 1,
    override val pageSize: Int = 10,
    override val filter: KSpecification<T>? = null,
    sortDirection: SortDirection,
) : Pageable, JimmerKFilterable<T>