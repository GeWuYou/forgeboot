package com.gewuyou.forgeboot.security.core.common.customizer

import org.springframework.security.config.web.server.ServerHttpSecurity

/**
 * 服务器 HTTP 安全定制器接口
 *
 * 该接口用于定义对 Spring Security 的 ServerHttpSecurity 配置的自定义逻辑。
 * 实现类可以通过重写 customize 方法来添加或修改安全配置规则。
 *
 * @since 2025-06-24 10:45:42
 * @author gewuyou
 */
interface ServerHttpSecurityCustomizer {

    /**
     * 判断当前定制器是否支持处理指定的安全链配置
     *
     * 此方法用于标识该定制器是否适用于特定的安全链配置。
     * 实现类应根据 chainId 参数决定是否启用此定制器的逻辑。
     *
     * @param chainId 安全链的唯一标识符，用于区分不同的安全配置场景
     * @return Boolean 返回 true 表示支持该 chainId，否则不支持
     */
    fun supports(chainId: String): Boolean

    /**
     * 自定义 ServerHttpSecurity 配置的方法
     *
     * 此方法由框架调用，允许开发者插入自定义的安全配置逻辑。
     * 方法参数提供了 ServerHttpSecurity 实例，可用于链式配置。
     *
     * @param http ServerHttpSecurity 实例，用于构建 WebFlux 安全配置
     */
    fun customize(http: ServerHttpSecurity)
}