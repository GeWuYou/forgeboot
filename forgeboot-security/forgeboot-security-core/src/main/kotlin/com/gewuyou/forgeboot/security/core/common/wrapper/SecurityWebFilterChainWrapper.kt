package com.gewuyou.forgeboot.security.core.common.wrapper

import com.gewuyou.forgeboot.security.core.common.identifier.SecurityFilterChainIdentifier
import org.springframework.security.web.server.SecurityWebFilterChain

/**
 * Forge Boot Security Web 过滤器链包装器
 *
 * 该类是一个 Kotlin 实现的 [SecurityWebFilterChain] 包装器，用于代理原始的 [SecurityWebFilterChain] 实例，
 * 并通过实现 [SecurityFilterChainIdentifier] 接口提供过滤器链标识符功能。
 *
 * @property delegate 被包装的 [SecurityWebFilterChain] 实例，用于执行实际的安全过滤逻辑。
 * @property chainId  当前过滤器链的唯一标识符，用于区分不同的安全配置链。
 *
 * @see SecurityWebFilterChain
 * @see SecurityFilterChainIdentifier
 */
class SecurityWebFilterChainWrapper(
    private val delegate: SecurityWebFilterChain,
    private val chainId: String
) : SecurityWebFilterChain by delegate, SecurityFilterChainIdentifier {
    /**
     * 获取当前过滤器链的唯一标识符。
     *
     * @return 返回当前链的标识符 [chainId]
     */
    override fun getChainId(): String = chainId
}