package com.gewuyou.webmvc.spec.core.extension

import com.gewuyou.forgeboot.webmvc.dto.PageResult
import org.springframework.data.domain.Page

/**
 * 分页扩展
 *
 * 该扩展函数用于将Spring Data的Page对象转换为自定义的PageResult对象
 * 主要目的是为了统一分页数据的封装格式，便于在不同层次之间传递和使用
 *
 * @param <T> 泛型参数，表示分页数据中的具体类型
 * @return 返回转换后的PageResult对象，包含分页查询的结果
 * @since 2025-05-29 21:51:52
 * @author gewuyou
 */
fun <T> Page<T>.toPageResult(): PageResult<T> {
    // 将当前分页信息和数据内容封装到自定义的PageResult对象中
    return PageResult.of(
        currentPage = this.number.toLong(),
        pageSize = this.size.toLong(),
        totalRecords = this.totalElements,
        records = this.content
    )
}

