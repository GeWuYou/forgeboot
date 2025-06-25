package com.gewuyou.forgeboot.security.authorize.autoconfigure

import com.gewuyou.forgeboot.security.authorize.api.core.config.SecurityAuthorizeProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 *forge security 授权自动配置
 *
 * @since 2025-06-15 19:28:11
 * @author gewuyou
 */
@Configuration
@EnableConfigurationProperties(SecurityAuthorizeProperties::class)
class ForgeSecurityAuthorizeAutoConfiguration