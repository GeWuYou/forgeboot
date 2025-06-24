package com.gewuyou.forgeboot.security.authenticate.impl.strategy.context

import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.security.authenticate.api.strategy.LoginRequestConverterStrategy
import com.gewuyou.forgeboot.security.core.authenticate.entities.request.LoginRequest
import jakarta.annotation.PostConstruct
import org.springframework.context.ApplicationContext
import org.springframework.security.core.Authentication
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.jvm.java

/**
 * 登录请求转换器上下文
 * 负责管理不同登录类型的请求转换策略，实现策略模式的上下文类
 *
 * @since 2025-06-10 20:48:09
 * @author gewuyou
 */
class LoginRequestConverterContext(
    private val applicationContext: ApplicationContext
) {
    /**
     * 存储登录类型与对应转换策略的映射关系
     */
    private val converterStrategies = mutableMapOf<String, LoginRequestConverterStrategy>()

    /**
     * 初始化转换器策略
     * 通过Spring容器获取所有LoginRequestConverterStrategy实例
     * 并根据支持的登录类型注册到converterStrategies中
     */
    @PostConstruct
    fun initConverterStrategies() {
        val strategies = applicationContext.getBeansOfType(LoginRequestConverterStrategy::class.java).toMap()
        for ((_, strategy) in strategies) {
            val loginType = strategy.getSupportedLoginType()
            addConverter(loginType, strategy)
        }
    }

    /**
     * 添加登录请求转换器策略
     * @param loginType 登录类型标识符
     * @param strategy 转换器策略实现
     */
    private fun addConverter(loginType: String, strategy: LoginRequestConverterStrategy) {
        converterStrategies[loginType] = strategy
        log.info("为登录类型: $loginType 添加登录请求转换器策略 ${strategy::class.java.name}")
    }

    /**
     * 执行对应的转换策略
     * 根据登录请求类型查找并执行对应的转换器策略
     *
     * @param loginRequest 登录请求对象
     * @return Authentication 认证对象
     * @throws IllegalArgumentException 当找不到匹配的转换策略时抛出异常
     */
    fun executeStrategy(loginRequest: LoginRequest): Authentication {
        return converterStrategies[loginRequest.getType()]?.convert(loginRequest)
            ?: throw IllegalArgumentException("No converter strategy found for login type ${loginRequest.getType()}")
    }
}