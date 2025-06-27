package com.gewuyou.forgeboot.security.authenticate.autoconfigure

import com.gewuyou.forgeboot.security.authenticate.api.service.UserCacheService
import com.gewuyou.forgeboot.security.authenticate.impl.provider.impl.UsernamePasswordAuthenticationProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder

/**
 *安全提供程序自动配置
 *
 * @since 2025-06-26 22:33:53
 * @author gewuyou
 */
@Configuration(proxyBeanMethods = false)
class SecurityProviderAutoConfiguration {
    @Bean("usernamePasswordAuthenticationProvider")
    @ConditionalOnMissingBean
    fun usernamePasswordAuthenticationProvider(
        userCacheService: UserCacheService,
        userDetailsService: UserDetailsService,
        passwordEncoder: PasswordEncoder
    ): AuthenticationProvider {
        return UsernamePasswordAuthenticationProvider(
            userCacheService,
            userDetailsService,
            passwordEncoder
        )
    }

}