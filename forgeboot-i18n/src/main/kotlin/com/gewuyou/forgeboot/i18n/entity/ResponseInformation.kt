package com.gewuyou.forgeboot.i18n.entity

/**
 * 响应信息(用于对外提供i18n响应信息)
 *
 * @author gewuyou
 * @since 2024-11-26 15:43:06
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
