package com.gewuyou.forgeboot.core.extension

/**
 * 尝试将给定的字符串转换为指定枚举类型的枚举值
 * 如果转换失败或输入字符串为空，则返回null
 *
 * @param name 要转换为枚举值的字符串名称，可以为空
 * @return 成功转换的枚举值，如果失败则返回null
 */
inline fun <reified T : Enum<T>> enumValueOfOrNull(name: String?): T? =
    try { enumValueOf<T>(name ?: "") } catch (_: Exception) { null }
