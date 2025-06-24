package com.gewuyou.forgeboot.cache.impl.utils

import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

/**
 * SpEL 解析器，用于解析和执行 Spring Expression Language (SpEL) 表达式。
 *
 * @since 2025-06-18 20:44:10
 * @author gewuyou
 */
object SpELResolver {
    /**
     * 表达式解析器实例，用于解析 SpEL 表达式。
     */
    private val parser: ExpressionParser = SpelExpressionParser()

    /**
     * 解析给定的 SpEL 表达式，并使用提供的参数进行求值。
     *
     * @param expression SpEL 表达式字符串
     * @param argsMap 包含变量及其对应值的映射表，用于表达式求值
     * @return 解析后的字符串结果，如果表达式返回 null，则返回空字符串
     */
    fun parse(expression: String, argsMap: Map<String, Any?>): String {
        // 创建评估上下文并设置变量
        val context = StandardEvaluationContext().apply {
            argsMap.forEach { (name, value) -> setVariable(name, value) }
        }

        // 解析并获取表达式的字符串值
        return parser.parseExpression(expression).getValue(context, String::class.java) ?: ""
    }
}