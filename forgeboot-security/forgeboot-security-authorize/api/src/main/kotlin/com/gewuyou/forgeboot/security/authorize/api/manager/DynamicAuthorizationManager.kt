package com.gewuyou.forgeboot.security.authorize.api.manager

import org.springframework.security.authorization.AuthorizationManager

/**
 * 动态授权管理器接口
 * 
 * 该接口用于处理请求级别的动态权限控制逻辑，基于Spring Security的AuthorizationManager接口进行扩展。
 * 通过泛型类型T定义需要处理的授权上下文类型，例如Web请求或方法调用等。
 *
 * @param <T> 授权操作涉及的具体上下文类型，如RequestAuthorizationContext或其他自定义上下文对象
 * 
 * @since 2025-06-24 15:52:01
 * @author gewuyou
 */
interface DynamicAuthorizationManager<T> : AuthorizationManager<T>
