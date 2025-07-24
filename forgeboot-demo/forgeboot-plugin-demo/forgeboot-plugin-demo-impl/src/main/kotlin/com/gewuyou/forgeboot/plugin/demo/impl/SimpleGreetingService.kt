package com.gewuyou.forgeboot.plugin.demo.impl

import com.gewuyou.forgeboot.plugin.demo.api.GreetingProvider
import com.gewuyou.forgeboot.plugin.demo.api.GreetingService
import org.pf4j.Extension

/**
 *简单的问候服务
 *
 * @since 2025-07-24 12:08:15
 * @author gewuyou
 */
@Extension
class SimpleGreetingService (
    private val greetingProvider: GreetingProvider
): GreetingService {
    override fun greet(name: String) = "${greetingProvider.message()}, $name from plugin!"
}
