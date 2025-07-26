package com.gewuyou.forgeboot.plugin.core.exception

/**
 *插件初始化异常
 *
 * @since 2025-07-26 11:25:18
 * @author gewuyou
 */
class PluginInitializationException(
    message: String,
    cause: Throwable?,
) : RuntimeException(
    message,
    cause
)