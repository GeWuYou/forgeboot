package com.gewuyou.forgeboot.banner.api.config.entities

import com.gewuyou.forgeboot.banner.api.enums.BannerStrategy
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 *横幅配置
 *
 * @since 2025-06-03 12:55:18
 * @author gewuyou
 */
@ConfigurationProperties("forgeboot.banner")
class BannerProperties {
    var path: String = "banners/"
    var  strategy: BannerStrategy = BannerStrategy.Random
}