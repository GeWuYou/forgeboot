package com.gewuyou.forgeboot.i18n.config.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * i18n属性
 *
 * @author gewuyou
 * @since 2025-02-18 23:59:57
 */
@ConfigurationProperties(prefix = "forgeboot.i18n")
public class I18nProperties {
    /**
     * 默认语言
     */
    private String defaultLocale = "zh_CN";

    /**
     * 语言请求参数名
     */
    private String langRequestParameter = "lang";
    /**
     * 语言文件路径
     */
    private String wildPathForLanguageFiles = "classpath*:i18n/**/messages";

    /**
     * 位置模式后缀
     */
    private String locationPatternSuffix = ".properties";

    public String getLocationPatternSuffix() {
        return locationPatternSuffix;
    }

    public void setLocationPatternSuffix(String locationPatternSuffix) {
        this.locationPatternSuffix = locationPatternSuffix;
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String getLangRequestParameter() {
        return langRequestParameter;
    }

    public void setLangRequestParameter(String langRequestParameter) {
        this.langRequestParameter = langRequestParameter;
    }

    public String getWildPathForLanguageFiles() {
        return wildPathForLanguageFiles;
    }

    public void setWildPathForLanguageFiles(String wildPathForLanguageFiles) {
        this.wildPathForLanguageFiles = wildPathForLanguageFiles;
    }
}
