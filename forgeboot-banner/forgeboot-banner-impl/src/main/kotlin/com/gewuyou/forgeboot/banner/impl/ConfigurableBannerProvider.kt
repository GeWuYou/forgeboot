package com.gewuyou.forgeboot.banner.impl

import com.gewuyou.forgeboot.banner.api.enums.BannerStrategy
import com.gewuyou.forgeboot.banner.api.provider.BannerProvider
import com.gewuyou.forgeboot.banner.api.config.entities.BannerProperties
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.io.PrintStream
import java.util.Random
import java.util.Scanner


/**
 * 可配置横幅提供者类，根据配置属性输出横幅
 *
 * @param properties 横幅属性配置对象
 */
class ConfigurableBannerProvider(
    private val properties: BannerProperties,
): BannerProvider {
    /**
     * 输出横幅内容到指定的输出流
     *
     * @param out 输出流对象，可为空
     */
    override fun printBanner(out: PrintStream) {
        try {
            // 根据属性配置获取横幅资源路径模式
            val pattern = "classpath*:" + properties.path + "*.txt"
            // 使用路径匹配获取所有符合条件的资源
            val resources: Array<Resource?> = PathMatchingResourcePatternResolver().getResources(pattern)
            // 如果没有找到任何资源，则输出提示信息并返回
            if (resources.isEmpty()) {
                out.println("No banner found in: " + properties.path)
                return
            }
            // 根据配置的策略选择横幅输出方式
            when (properties.strategy) {
                BannerStrategy.First-> resources[0]?.let { printResource(it, out) }
                BannerStrategy.Random -> resources[Random().nextInt(resources.size)]?.let { printResource(it, out) }
                BannerStrategy.All -> {
                    for (resource in resources) {
                        resource?.let { printResource(it, out) }
                        out.println()
                    }
                }
            }
        } catch (e: Exception) {
            // 异常处理：输出错误信息
            out.println("Error loading banners: " + e.message)
        }
    }

    /**
     * 打印资源内容到指定的输出流
     *
     * @param resource 要打印的资源对象
     * @param out 输出流对象
     */
    private fun printResource(resource: Resource, out: PrintStream) {
        try {
            // 使用输入流读取资源内容并输出
            resource.inputStream.use { `is` ->
                Scanner(`is`).useDelimiter("\\A").use { scanner ->
                    while (scanner.hasNext()) {
                        out.println(scanner.next())
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            // 异常处理：输出错误信息
            out.println("Failed to print banner: " + e.message)
        }
    }
}
