package com.gewuyou.forgeboot.webmvc.exception.core

/**
 *内部异常
 *
 * @since 2025-05-30 14:16:01
 * @author gewuyou
 */
class InternalException(
    message: String = "内部未知异常",
    cause: Throwable? = null
) : RuntimeException(message, cause)