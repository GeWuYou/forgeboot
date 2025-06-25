package com.gewuyou.forgeboot.security.authorize.api.webflux.manager

import org.springframework.security.authorization.ReactiveAuthorizationManager

/**
 *动态反应式授权管理器
 *
 * @since 2025-06-24 13:16:25
 * @author gewuyou
 */
fun interface DynamicReactiveAuthorizationManager<T> : ReactiveAuthorizationManager<T>
