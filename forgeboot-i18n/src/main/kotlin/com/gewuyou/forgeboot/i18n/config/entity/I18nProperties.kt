package com.gewuyou.forgeboot.i18n.config.entity

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * i18n属性
 *
 * @author gewuyou
 * @since 2025-02-18 23:59:57
 */
@ConfigurationProperties(prefix = "base-forge.i18n")
class I18nProperties {
    /**
     * 默认语言
     */
    var defaultLocale = "zh_CN"

    /**
     * 语言请求参数名
     */
    var langRequestParameter = "lang"
    /**
     * 语言文件路径
     */
    var wildPathForLanguageFiles = "classpath*:i18n/**/messages"
}
