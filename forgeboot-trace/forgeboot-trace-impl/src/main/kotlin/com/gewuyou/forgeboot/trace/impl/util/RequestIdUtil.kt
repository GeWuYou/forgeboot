package com.gewuyou.forgeboot.trace.impl.util

import java.util.*

/**
 * 请求 ID Util
 * 这个类需配合 RequestIdFilter 使用，用于生成请求 ID，并将其绑定到线程变量中，供后续可能需要的地方使用。
 * @author gewuyou
 * @since 2025-01-02 14:27:45
 */
 object RequestIdUtil {
    private val REQUEST_ID_HOLDER = ThreadLocal<String>()
    fun generateRequestId() {
        REQUEST_ID_HOLDER.set(UUID.randomUUID().toString())
    }

    var requestId: String?
        get() = REQUEST_ID_HOLDER.get()
        set(uuid) {
            REQUEST_ID_HOLDER.set(uuid)
        }

    fun removeRequestId() {
        REQUEST_ID_HOLDER.remove()
    }
}
