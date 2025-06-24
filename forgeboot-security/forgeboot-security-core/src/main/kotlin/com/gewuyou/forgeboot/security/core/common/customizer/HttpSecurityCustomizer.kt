package com.gewuyou.forgeboot.security.core.common.customizer

import org.springframework.security.config.annotation.web.builders.HttpSecurity

/**
 * HTTP 安全定制器接口
 *
 * 该函数式接口用于定义 HTTP 安全配置的定制逻辑，通常用于
 * Spring Security 的 HttpSecurity 配置阶段。
 *
 * @since 2025-06-24 10:46:32
 * @author gewuyou
 */
interface HttpSecurityCustomizer {
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
     * 执行安全配置的定制逻辑
     *
     * @param http 用于构建 HTTP 安全策略的 HttpSecurity 实例
     *             通过此参数可添加或修改安全规则，如认证、授权等
     */
    fun customize(http: HttpSecurity)
}