package com.gewuyou.forgeboot.security.authorize.api.core.validator

/**
 * 令牌验证器接口，用于定义通用的令牌验证逻辑
 *
 * 该接口设计为泛型接口，支持不同类型的令牌验证结果。
 *
 * @since 2025-06-26 15:54:14
 * @author gewuyou
 */
fun interface TokenValidator<T> {
    /**
     * 验证指定的令牌字符串并返回解析后的结果对象
     *
     * @param token 待验证的令牌字符串，通常由客户端在请求头中提供
     * @return 返回解析后的泛型对象 T，可能是用户信息、权限列表或其他业务相关的数据结构
     * @throws IllegalArgumentException 如果令牌格式不正确或为空
     * @throws SecurityException 如果令牌无效或已过期
     */
    fun validate(token: String): T
}