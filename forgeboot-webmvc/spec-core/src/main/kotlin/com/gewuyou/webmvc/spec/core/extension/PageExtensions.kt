/*
 *
 *  * Copyright (c) 2025
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 *
 */

package com.gewuyou.webmvc.spec.core.extension

import com.gewuyou.forgeboot.webmvc.dto.api.entities.PageResult
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

