package com.gewuyou.forgeboot.webmvc.exception.core

import com.gewuyou.forgeboot.webmvc.dto.ResponseInformation

/**
 *全局异常
 *
 * @since 2025-05-30 13:25:17
 * @author gewuyou
 */
open class GlobalException(
    val responseInformation: ResponseInformation,
    cause: Throwable? = null
): RuntimeException(responseInformation.responseMessage(), cause)