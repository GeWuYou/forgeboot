package com.gewuyou.forgeboot.security.authenticate.impl.strategy.context

import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.security.authenticate.api.enums.LoginTypes

/**
 * 抽象处理程序上下文
 * 用于构建策略处理器的通用框架，支持通过类型解析对应的处理器实例
 *
 * @since 2025-06-11 21:59:40
 * @author gewuyou
 */
abstract class AbstractHandlerContext<T : Any, H : Any>(
    /**
     * 策略对象列表，用于从中提取类型和对应的处理器
     */
    strategies: List<T>,
    /**
     * 当前处理器类型的名称，用于日志输出时标识处理器种类
     */
    private val typeName: String,
    /**
     * 提取策略对应类型的函数，用于确定每个策略的标识
     */
    extractType: (T) -> String,
    /**
     * 提取策略对应处理器的函数，用于获取实际可执行的处理器逻辑
     */
    extractHandler: (T) -> H
) {
    /**
     * 默认处理器，用于处理未指定登录类型的请求
     */
    private var defaultStrategy: T? = null
    /**
     * 存储登录类型与处理器之间的映射关系
     */
    private val handlerMap: Map<String, H>

    init {
        val map = mutableMapOf<String, H>()
        for (strategy in strategies) {
            // 提取当前策略的登录类型标识
            val loginType = extractType(strategy)
            if (loginType == LoginTypes.DEFAULT) {
                defaultStrategy = strategy
                continue
            }
            // 获取当前策略对应的处理器实例
            val handler = extractHandler(strategy)
            // 检查是否已存在相同类型的处理器
            val existingHandlerName = map[loginType]?.javaClass?.name
            existingHandlerName?.let {
                log.warn("重复注册登录类型 [$loginType] 的$typeName，已存在 $existingHandlerName，被 ${strategy::class.java.name} 覆盖")
            }?.run {
                log.info("注册${typeName}策略: $loginType -> ${strategy::class.java.name}")
            }
            // 注册或覆盖处理器映射
            map[loginType] = handler
        }
        // 不可变化处理器映射表
        this.handlerMap = map.toMap()
    }

    /**
     * 根据指定的登录类型解析对应的处理器实例
     *
     * 该方法首先尝试通过给定的登录类型从处理器映射中查找对应的策略处理器。
     * 如果找不到匹配项，则尝试使用默认类型的处理器。
     * 如果仍然无法找到，默认处理器也不存在，则抛出异常。
     *
     * @param loginType 登录类型标识，用于查找对应的处理器
     * @return H 类型的处理器实例，与给定的登录类型或默认类型匹配
     * @throws IllegalArgumentException 如果既没有找到与登录类型匹配的处理器，也没有定义默认处理器
     */
    fun resolve(loginType: String): H {
        return handlerMap[loginType]
            ?: handlerMap[LoginTypes.DEFAULT]
            ?: throw IllegalArgumentException("未找到 loginType 为 [$loginType] 的$typeName，且未定义默认$typeName")
    }

}