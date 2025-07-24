package com.gewuyou.webmvc.spec.jpa.extension

import com.gewuyou.forgeboot.webmvc.dto.PageResult

/**
 * 分页结果扩展
 *
 * 该扩展函数用于将分页结果中的每个元素转换为另一种类型，同时保持分页信息不变
 * 它接受一个转换函数作为参数，该函数定义了如何将分页结果中的每个元素从类型 T 转换为类型 R
 *
 * @param transform 转换函数，用于将分页结果中的每个元素从类型 T 转换为类型 R
 * @return 返回一个新的分页结果对象，其中包含转换后的元素
 * @since 2025-05-29 22:05:37
 * @author gewuyou
 */
fun <T, R> PageResult<T>.map(transform: (T) -> R): PageResult<R> {
    return PageResult.of(
        currentPage = this.currentPage,
        pageSize = this.pageSize,
        totalRecords = this.totalRecords,
        records = this.records?.map(transform)?.toMutableList() ?: mutableListOf()
    )
}
