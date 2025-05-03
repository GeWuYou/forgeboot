package com.gewuyou.forgeboot.common.result.impl

import com.gewuyou.forgeboot.common.result.api.RequestIdProvider

/**
 *默认请求ID提供商
 *
 * @since 2025-05-03 16:22:18
 * @author gewuyou
 */
object DefaultRequestIdProvider : RequestIdProvider {
    override fun getRequestId(): String = ""
}