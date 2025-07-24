package com.gewuyou.forgeboot.plugin.demo.api

import org.pf4j.ExtensionPoint

/**
 *问候服务
 *
 * @since 2025-07-23 14:04:12
 * @author gewuyou
 */
fun interface GreetingService: ExtensionPoint {
    fun greet(name: String): String
}