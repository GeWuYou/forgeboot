package com.gewuyou.forgeboot.webmvc.dto

import java.io.Serializable

/**
 *基本统一响应封装类
 *
 * @since 2025-05-30 14:01:14
 * @author gewuyou
 */
open class BaseResult<T>(
    open val code: Any,
    open val success: Boolean,
    open val message: String,
    open val data: T? = null,
    open val requestId: String? = null,
    open val extra: Map<String, Any?> = emptyMap(),
) : Serializable {

    fun toMutableFlatMap(): MutableMap<String, Any?> {
        val map = mutableMapOf(
            "code" to code,
            "success" to success,
            "message" to message,
            "data" to data
        )
        if (!requestId.isNullOrBlank()) {
            map["requestId"] = requestId
        }
        map.putAll(extra)
        return map
    }

    fun toFlatMap(): Map<String, Any?> = toMutableFlatMap().toMap()
}