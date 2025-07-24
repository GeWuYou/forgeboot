package com.gewuyou.webmvc.spec.core.page

/**
 * 可过滤接口，用于支持泛型类型的过滤操作
 *
 *
 * @param <FilterType> 过滤条件的类型，通常是一个包含过滤参数的数据传输对象（DTO）。
 *
 * @since 2025-07-19 08:56:56
 * @author gewuyou
 */
interface Filterable<FilterType> : QueryComponent {
    /**
     * 获取当前的过滤条件对象。
     *
     * @return 返回一个可空的 FilterType 实例，表示当前的过滤条件。
     */
    val filter: FilterType?
}
