package com.gewuyou.forgeboot.webmvc.version.config.entities

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 版本属性
 *
 *
 * 该类用于配置和管理版本相关的属性，通过@ConfigurationProperties注解
 * 将其与配置文件中以 version 为前缀的属性自动绑定
 *
 * @author gewuyou
 * @since 2025-05-02 11:52:24
 */
@ConfigurationProperties(prefix = "forgeboot.webmvc.version")
class WebMvcVersionProperties {
    /**
     * API前缀
     *
     *
     * 定义了API的路由前缀，用于在URL中区分不同的API版本
     */
    var apiPrefix: String = "/api"

    /**
     * API后缀
     *
     *
     * 定义了API的路由后缀，用于在URL中区分不同的API版本
     */
    var apiSuffix: String = ""
}
