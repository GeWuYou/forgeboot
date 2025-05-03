package com.gewuyou.forgeboot.common.result.impl

import com.gewuyou.forgeboot.common.result.api.MessageResolver

/**
 *默认消息解析器
 *
 * @since 2025-05-03 16:21:43
 * @author gewuyou
 */
object DefaultMessageResolver : MessageResolver {
    override fun resolve(code: String, args: Array<Any>? ): String = code
}