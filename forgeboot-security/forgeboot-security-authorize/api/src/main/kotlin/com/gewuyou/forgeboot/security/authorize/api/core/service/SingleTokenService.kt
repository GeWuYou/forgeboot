package com.gewuyou.forgeboot.security.authorize.api.core.service

import com.gewuyou.forgeboot.security.core.authorize.entities.SingleTokenPrincipal

/**
 * 单一令牌验证服务接口
 *
 * 该接口用于定义对单一令牌的验证操作，通过提供的令牌字符串返回对应的主体信息。
 *
 * @since 2025-06-25 13:10:38
 * @author gewuyou
 */
fun interface SingleTokenService {

    /**
     * 验证给定的令牌字符串并返回对应的主体信息。
     *
     * @param token 要验证的令牌字符串
     * @return 返回与令牌关联的主体信息对象 [SingleTokenPrincipal]
     */
    fun validate(token: String): SingleTokenPrincipal
}