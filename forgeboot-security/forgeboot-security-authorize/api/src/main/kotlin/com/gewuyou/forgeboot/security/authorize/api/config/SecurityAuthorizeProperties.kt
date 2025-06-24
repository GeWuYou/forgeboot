package com.gewuyou.forgeboot.security.authorize.api.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 *security 授权属性
 *
 * @since 2025-06-15 19:26:20
 * @author gewuyou
 */
@ConfigurationProperties(prefix = "forgeboot.security.authorize")
class SecurityAuthorizeProperties {
    var defaultExceptionResponse: String = "Sorry, you don't have access to the resource!"
}