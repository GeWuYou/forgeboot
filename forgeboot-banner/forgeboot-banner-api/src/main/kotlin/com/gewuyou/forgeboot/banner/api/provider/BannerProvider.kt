package com.gewuyou.forgeboot.banner.api.provider

import java.io.PrintStream

/**
 * 横幅提供商接口，用于定义横幅输出行为
 *
 * @since 2025-06-03 12:17:19
 * @author gewuyou
 */
fun interface BannerProvider {
    /**
     * 输出横幅内容到指定的输出流
     *
     * @param out 输出流对象
     */
    fun printBanner(out: PrintStream)
}