package com.gewuyou.webmvc.spec.core.enums

import java.util.Locale

/**
 * 排序方向枚举
 *
 * @author gewuyou
 * @since 2025-01-16 17:56:01
 */
enum class SortDirection {
    ASC, DESC;

    /**
     * 获取当前排序方向的反方向
     *
     * @return 返回与当前方向相反的排序方向枚举值
     *         - 当前为ASC时返回DESC
     *         - 当前为DESC时返回ASC
     */
    fun reverse(): SortDirection = when (this) {
        ASC -> DESC
        DESC -> ASC
    }

    companion object {
        /**
         * 将字符串解析为排序方向枚举值（忽略大小写）
         *
         * 支持的输入包括：
         * - "asc", "ascending" → ASC
         * - "desc", "descending" → DESC
         *
         * @param value 待解析的字符串，可为空
         * @return 解析成功返回对应的枚举值，value为空时返回null，无法解析时抛出异常
         * @throws IllegalArgumentException 当输入字符串无法识别为有效的排序方向时
         */
        @JvmStatic
        fun interpret(value: String?): SortDirection? {
            if (value == null) return null
            return when (value.lowercase(Locale.ROOT)) {
                "asc", "ascending" -> ASC
                "desc", "descending" -> DESC
                else -> throw IllegalArgumentException("Unknown sort direction: $value")
            }
        }

        /**
         * 安全地将字符串解析为排序方向枚举值，如果解析失败则返回默认值
         *
         * @param value 待解析的字符串，可为空
         * @param default 解析失败时返回的默认值，默认为ASC
         * @return 解析成功返回对应的枚举值，解析失败或value为空时返回默认值
         */
        fun interpretOrDefault(value: String?, default: SortDirection = ASC): SortDirection {
            return try {
                interpret(value) ?: default
            } catch (_: IllegalArgumentException) {
                default
            }
        }
    }

}
