package com.gewuyou.forgeboot.security.authenticate.api.strategy

import com.gewuyou.forgeboot.security.core.authenticate.entities.request.LoginRequest
import org.springframework.security.core.Authentication

/**
 *登录请求转换器策略
 * 请注意，该接口仅用于定义转换器的接口只负责转换登录请求为认证对象，不负责提供认证逻辑。
 * 具体的认证逻辑由具体的认证提供类实现。因此当需要自定义认证逻辑时，请实现具体的认证提供类。
 * @since 2025-02-15 03:23:34
 * @author gewuyou
 */
interface LoginRequestConverterStrategy {
    /**
     * 转换登录请求为认证对象
     * @param loginRequest 登录请求
     * @return 认证对象
     */
    fun convert(loginRequest: LoginRequest): Authentication

    /**
     * 获取支持的登录类型
     * @return 请求的登录类型
     */
    fun getSupportedLoginType(): String
}