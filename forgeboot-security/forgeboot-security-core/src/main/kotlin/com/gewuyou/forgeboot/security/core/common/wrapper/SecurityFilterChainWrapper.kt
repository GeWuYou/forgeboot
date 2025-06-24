package com.gewuyou.forgeboot.security.core.common.wrapper

import com.gewuyou.forgeboot.security.core.common.identifier.SecurityFilterChainIdentifier
import org.springframework.security.web.SecurityFilterChain

/**
 * 安全过滤器链包装器
 *
 * 该类为 [SecurityFilterChain] 提供了装饰功能，同时实现了 [SecurityFilterChainIdentifier] 接口，
 * 用于标识当前安全过滤器链的唯一 ID。
 *
 * @property delegate 被包装的真实 [SecurityFilterChain] 实例，所有方法调用将委托给它。
 * @property chainId 当前过滤器链的唯一标识符。
 *
 * @see SecurityFilterChain
 * @see SecurityFilterChainIdentifier
 *
 * @since 2025-06-24 16:11:43
 * @author gewuyou
 */
class SecurityFilterChainWrapper(
    private val delegate: SecurityFilterChain,
    private val chainId: String,
) : SecurityFilterChain by delegate, SecurityFilterChainIdentifier {
    /**
     * 获取当前过滤器链的唯一标识符。
     *
     * @return 返回当前链的标识符 [chainId]
     */
    override fun getChainId(): String = chainId
}