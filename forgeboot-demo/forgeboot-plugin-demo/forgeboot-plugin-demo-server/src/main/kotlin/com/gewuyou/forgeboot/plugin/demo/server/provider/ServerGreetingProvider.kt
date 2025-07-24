package com.gewuyou.forgeboot.plugin.demo.server.provider

import com.gewuyou.forgeboot.plugin.demo.api.GreetingProvider
import org.springframework.stereotype.Component

/**
 *服务器问候提供商
 *
 * @since 2025-07-24 15:10:38
 * @author gewuyou
 */
@Component
class ServerGreetingProvider: GreetingProvider {
    override fun message(): String = "服务器问候"
}