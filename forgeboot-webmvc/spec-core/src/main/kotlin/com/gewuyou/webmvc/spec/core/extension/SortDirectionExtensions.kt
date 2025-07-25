package com.gewuyou.webmvc.spec.core.extension

import com.gewuyou.webmvc.spec.core.enums.SortDirection
import org.springframework.data.domain.Sort

/**
 * 将自定义的排序方向枚举转换为 Spring Data的排序方向
 *
 * @return 对应的 Spring Data Sort.Direction 枚举值
 */
fun SortDirection.toSpringDirection(): Sort.Direction =
    if (this == SortDirection.DESC) Sort.Direction.DESC else Sort.Direction.ASC
