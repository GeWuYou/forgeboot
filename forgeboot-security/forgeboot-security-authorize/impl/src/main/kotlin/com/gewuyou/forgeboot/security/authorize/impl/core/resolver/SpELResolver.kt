package com.gewuyou.forgeboot.security.authorize.impl.core.resolver

import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.expression.BeanFactoryResolver
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

/**
 * SpEL 解析器，用于解析和执行 Spring Expression Language (SpEL) 表达式。
 *
 * 该类封装了 SpEL 的解析和求值过程，结合 Spring 的 BeanFactory 支持表达式中对 Bean 的引用。
 *
 * @property beanFactory 提供 Spring 容器的 Bean 工厂，用于解析表达式中的 Bean 引用。
 * @since 2025-06-15 13:32:31
 * @author gewuyou
 */
class SpELResolver(private val beanFactory: ConfigurableBeanFactory) {
    // 初始化 SpEL 表达式解析器
    private val parser = SpelExpressionParser()
    // 初始化 SpEL 求值上下文，并设置 Bean 解析器
    private val context = StandardEvaluationContext()

    /**
     * 在初始化阶段配置上下文，使其能够通过 BeanFactory 解析表达式中的 Bean。
     */
    init {
        context.beanResolver = BeanFactoryResolver(beanFactory)
    }

    /**
     * 解析并计算给定的 SpEL 表达式，返回结果作为字符串。
     *
     * @param expression 表达式字符串，例如 `#user.name` 或 `'Hello ' + #name`
     * @param rootObject 根对象，表达式中的属性访问将基于此对象进行解析
     * @return 解析后的字符串结果，如果表达式结果为 null，则返回空字符串
     */
    fun parse(expression: String, rootObject: Any): String {
        context.setRootObject(rootObject)

        // 如果是 Map，则把每个键当作变量注入，支持 #varName 直接取值
        if (rootObject is Map<*, *>) {
            rootObject.forEach { (k, v) ->
                if (k is String) context.setVariable(k, v)
            }
        }
        return parser
            .parseExpression(expression)
            .getValue(context, String::class.java) ?: ""
    }
}