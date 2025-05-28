package com.gewuyou.forgeboot.i18n.api

/**
 * 响应信息
 *
 * @author gewuyou
 * @since 2025-05-03 16:31:59
 */
interface ResponseInformation {
    /**
     * 获取响应码
     * @return 响应码
     */
    val responseCode: Int

    /**
     * 获取i18n响应信息code
     * @return 响应信息 code
     */
    val responseI8nMessageCode: String
}
