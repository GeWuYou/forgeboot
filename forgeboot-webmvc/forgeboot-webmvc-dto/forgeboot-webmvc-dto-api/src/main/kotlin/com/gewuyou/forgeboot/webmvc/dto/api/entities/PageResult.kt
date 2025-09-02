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

package com.gewuyou.forgeboot.webmvc.dto.api.entities

/**
 * 分页结果返回对象
 *
 * 通常用于封装分页查询的返回结果，包括总数、页码等信息。
 *
 * @author gewuyou
 * @since 2024-04-23 下午10:53:04
 */
open class PageResult<T> {

    /**
     * 当前页的记录列表
     */
    var records: MutableList<T>? = mutableListOf()

    /**
     * 总记录数（整个数据集的记录总数）
     */
    var totalRecords: Long = 0L

    /**
     * 总页数（根据每页大小计算出的总页数）
     */
    var totalPages: Long = 0L

    /**
     * 当前页码（从1开始计数）
     */
    var currentPage: Long = 1L

    /**
     * 每页显示的记录数量
     */
    var pageSize: Long = 10L

    /**
     * 是否存在上一页
     */
    var hasPrevious: Boolean = false

    /**
     * 是否存在下一页
     */
    var hasNext: Boolean = false

    companion object {

        /**
         * 创建一个空的分页结果实例
         *
         * @return 新的PageResult实例
         */
        fun <T> of(): PageResult<T> = PageResult()

        /**
         * 根据指定参数创建分页结果
         *
         * @param currentPage   当前页码
         * @param pageSize      每页大小
         * @param totalRecords  总记录数
         * @param records       当前页的记录列表
         * @return 填充好的PageResult实例
         */
        fun <T> of(currentPage: Long, pageSize: Long, totalRecords: Long, records: MutableList<T>?): PageResult<T> {
            val result = PageResult<T>()
            result.records = records
            result.totalRecords = totalRecords
            result.pageSize = pageSize
            result.currentPage = currentPage
            result.totalPages = (totalRecords + pageSize - 1) / pageSize
            result.hasPrevious = currentPage > 1
            result.hasNext = currentPage < result.totalPages
            return result
        }

        /**
         * 根据指定参数创建分页结果（支持Int参数）
         *
         * @param currentPage   当前页码
         * @param pageSize      每页大小
         * @param totalRecords  总记录数
         * @param records       当前页的记录列表
         * @return 填充好的PageResult实例
         */
        fun <T> of(currentPage: Int, pageSize: Int, totalRecords: Long, records: MutableList<T>?): PageResult<T> {
            return of(currentPage.toLong(), pageSize.toLong(), totalRecords, records)
        }

        /**
         * 创建一个仅设置页码和页面大小的分页结果
         *
         * @param currentPage   当前页码
         * @param pageSize      每页大小
         * @return 部分填充的PageResult实例
         */
        fun <T> of(currentPage: Long, pageSize: Long): PageResult<T> {
            val result = PageResult<T>()
            result.currentPage = currentPage
            result.pageSize = pageSize
            return result
        }

        /**
         * 创建一个空的分页结果实例
         *
         * @return 新的PageResult实例
         */
        fun <T> empty(): PageResult<T> = PageResult()
    }
    override fun toString(): String {
        return "PageResult(records=$records, totalRecords=$totalRecords, totalPages=$totalPages, currentPage=$currentPage, pageSize=$pageSize, hasPrevious=$hasPrevious, hasNext=$hasNext)"
    }
}