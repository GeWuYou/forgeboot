package com.gewuyou.webmvc.spec.core.extension

import com.gewuyou.webmvc.spec.core.page.Pageable
import com.gewuyou.webmvc.spec.core.page.QueryComponent
import com.gewuyou.webmvc.spec.core.page.Sortable
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

/**
 * 将 QueryComponent 转换为 Spring Data 的 PageRequest 对象
 *
 *
 * @return PageRequest 分页请求对象，包含分页和排序信息
 *
 * 重要逻辑说明：
 * 1. currentPage 从 1 开始计数，转换为 Spring Data 从 0 开始的页码
 * 2. pageSize 保持不变，直接用于分页请求
 * 3. 如果对象实现了 Sortable 接口，则使用其排序条件；否则使用未排序状态
 * 4. 如果对象未实现 Pageable 接口，则使用默认分页参数（第一页，每页10条）
 */
fun QueryComponent.toPageRequest(): PageRequest {
    val pageable = this as? Pageable
    val sortable = this as? Sortable
    // 默认分页参数
    val page = pageable?.let { (it.currentPage - 1).coerceAtLeast(0) } ?: 0
    val size = pageable?.pageSize ?: 10
    // 排序规则
    val sort = sortable?.let { Sort.by(it.toSortOrders()) } ?: Sort.unsorted()
    return PageRequest.of(page, size, sort)
}