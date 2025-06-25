package com.gewuyou.forgeboot.security.authorize.api.core.strategy

import com.gewuyou.forgeboot.security.authorize.api.core.provider.PermissionProvider
import org.springframework.security.core.Authentication

/**
 * 授权策略接口
 *
 * 定义了实现授权逻辑的统一接口，所有具体授权策略都需要实现该接口。
 * 该接口设计为函数式接口，便于通过Lambda表达式或函数引用实现简洁的策略定义。
 *
 * @since 2025-06-15 13:01:27
 * @author gewuyou
 */
fun interface AuthorizationStrategy {

    /**
     * 执行授权判断
     *
     * 根据提供的认证信息、权限标识和权限提供者列表，判断当前认证是否具备指定权限。
     *
     * @param authentication 当前用户的认证信息，包含主体身份和凭证等
     * @param permission 需要验证的权限标识，通常为特定资源的操作权限字符串
     * @param providers 权限提供者列表，用于获取与权限相关的数据源或校验逻辑
     * @return Boolean 返回授权结果，true表示授权通过，false表示拒绝访问
     */
    fun authorize(
        authentication: Authentication,
        permission: String,
        providers: List<PermissionProvider>
    ): Boolean
}
