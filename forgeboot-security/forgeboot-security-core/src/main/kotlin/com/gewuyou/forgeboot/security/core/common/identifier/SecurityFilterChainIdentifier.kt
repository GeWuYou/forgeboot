package com.gewuyou.forgeboot.security.core.common.identifier

/**
 * 安全过滤器链标识符函数式接口
 *
 * 该接口用于定义获取安全过滤器链唯一标识的方法。
 * 在多安全链环境中，每个实现类应提供一个唯一且可读的标识符，
 * 以便于日志记录、调试和配置识别。
 */
fun interface SecurityFilterChainIdentifier {

    /**
     * 获取当前过滤器链的唯一标识符。
     *
     * 在复杂的多安全链环境中，此方法返回的标识符用于唯一标识一个过滤器链实例。
     * 标识符应在实现类中确保其唯一性和可读性，通常用于日志记录、调试和配置识别。
     *
     * @return 当前过滤器链实例的唯一标识字符串，非空且不应重复。
     */
    fun getChainId(): String
}