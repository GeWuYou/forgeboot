package com.gewuyou.forgeboot.webmvc.dto

/**
 * 响应信息接口
 *
 * 该接口定义了响应信息的标准结构，包括响应状态码和响应消息
 * 主要用于规范响应数据的格式，以确保响应数据的一致性和可解析性
 *
 * @since 2025-05-30 13:27:27
 * @author gewuyou
 */
interface ResponseInformation {
    /**
     * 响应状态码，用于表示响应的状态
     */
    fun responseStateCode(): Int

    /**
     * 响应消息，用于提供更详细的响应信息
     */
    fun responseMessage(): String
}
