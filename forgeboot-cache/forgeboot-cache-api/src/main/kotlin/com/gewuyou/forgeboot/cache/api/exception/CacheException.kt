package com.gewuyou.forgeboot.cache.api.exception

/**
 * 缓存异常类，用于封装与缓存操作相关的异常信息。
 *
 * @param message 异常的详细描述信息，默认为 null。
 * @param cause   导致此异常的底层异常，默认为 null。
 *
 * @since 2025-06-17 21:14:40
 * @author gewuyou
 */
class CacheException(
    message: String? = null,
    cause: Throwable? = null,
) : RuntimeException(
    message, cause
)