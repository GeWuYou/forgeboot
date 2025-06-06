package com.gewuyou.forgeboot.i18n.api.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * i18n属性
 *
 * @author gewuyou
 * @since 2025-02-18 23:59:57
 */
@ConfigurationProperties(prefix = "forgeboot.i18n")
class I18nProperties {
    /**
     * 默认语言
     */
    var defaultLocale: String = "zh_CN"

    /**
     * 语言请求参数名
     */
    var langRequestParameter: String = "lang"

    /**
     * 语言文件路径
     */
    var wildPathForLanguageFiles: String = "classpath*:i18n/**/messages"

    /**
     * 位置模式后缀
     */
    var locationPatternSuffix: String = ".properties"
}
