package com.gewuyou.forgeboot.common.result.api
/**
 * 请求ID提供商接口
 *
 * 该接口用于获取请求ID，请求ID是用于跟踪和调试的唯一标识符
 * 它可以帮助开发者在日志和监控系统中追踪请求的来源和流向
 *
 * @since 2025-05-03 16:12:44
 * @author gewuyou
 */
fun interface RequestIdProvider {
    /**
     * 获取请求ID
     *
     * 返回一个唯一的字符串标识符作为请求ID
     *
     * @return 请求ID的字符串表示
     */
    fun getRequestId(): String
}
