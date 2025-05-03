package com.gewuyou.forgeboot.common.result.api
/**
 * 消息解析器接口
 *
 * 用于根据消息代码和参数解析出具体的消息文本
 * 这个接口的主要作用是定义一个标准的消息解析方法，以便在不同的上下文中解析消息
 *
 * @since 2025-05-03 16:10:26
 * @author gewuyou
 */
fun interface MessageResolver {
    /**
     * 解析消息
     *
     * 根据给定的消息代码和可选的参数数组，解析并返回具体的消息字符串
     * 这个方法允许在不同的上下文中重用消息解析逻辑，并可以根据需要提供不同的实现
     *
     * @param code 消息代码，用于标识特定的消息类型或模板
     * @param args 可选的消息参数数组，用于替换消息模板中的占位符
     * @return 解析后的消息字符串
     */
    fun resolve(code: String, args: Array<Any>?): String
}