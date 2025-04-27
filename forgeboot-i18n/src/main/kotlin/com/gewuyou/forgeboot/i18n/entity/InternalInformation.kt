package com.gewuyou.forgeboot.i18n.entity

/**
 * 内部信息(用于扩展项目内的i18n信息)
 *
 * @author gewuyou
 * @since 2024-11-26 17:14:15
 */
interface InternalInformation {
    /**
     * 获取i18n响应信息code
     * @return 响应信息 code
     */
    val responseI8nMessageCode: String?
}
