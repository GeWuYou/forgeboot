package com.gewuyou.forgeboot.plugin.demo.server.controller

import com.gewuyou.forgeboot.plugin.demo.api.GreetingService
import org.springframework.context.annotation.Lazy
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 *演示控制器
 *
 * @since 2025-07-24 12:37:26
 * @author gewuyou
 */
@RestController
class DemoController(
    @Lazy
    private val greetingService: GreetingService
) {
    @GetMapping("/plugin/greet")
    fun greet(@RequestParam("name") name: String) = greetingService.greet(name)
}