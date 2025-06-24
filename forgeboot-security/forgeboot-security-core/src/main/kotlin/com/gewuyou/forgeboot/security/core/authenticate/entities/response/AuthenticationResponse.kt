package com.gewuyou.forgeboot.security.core.authenticate.entities.response

/**
 * 身份验证响应基类
 *
 * 该类用于封装身份验证成功后的基础响应数据，包含认证主体和凭证信息。
 * 主要用于框架内部在完成身份验证后构造统一的响应结构。
 *
 * @property principal 认证主体对象，可以是用户信息或其他认证实体
 * @property credentials 认证凭证信息，例如密码、令牌等敏感数据载体
 *
 * @since 2025-06-10 15:49:03
 * @author gewuyou
 */
open class AuthenticationResponse(
    open val principal: Any? = null,
    open val credentials: Any? = null,
)