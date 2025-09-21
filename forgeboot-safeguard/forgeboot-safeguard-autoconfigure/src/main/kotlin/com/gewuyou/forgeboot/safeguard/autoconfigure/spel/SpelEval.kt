/*
 *
 *  * Copyright (c) 2025
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 *
 */

package com.gewuyou.forgeboot.safeguard.autoconfigure.spel

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.beans.factory.BeanFactory
import org.springframework.context.expression.BeanFactoryResolver
import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

/**
 * SpelEval 是一个用于执行 Spring Expression Language (SpEL) 表达式的工具对象。
 * 它提供了在特定上下文中解析和计算 SpEL 表达式的能力，支持访问方法参数和 Spring Bean。
 *
 * @since 2025-09-21 14:11:33
 * @author gewuyou
 */
object SpelEval {
    private val parser = SpelExpressionParser()
    private val names = DefaultParameterNameDiscoverer()

    /**
     * 执行给定的 SpEL 表达式并返回指定类型的计算结果。
     *
     * @param pjp ProceedingJoinPoint 对象，用于获取方法签名和参数信息
     * @param beanFactory BeanFactory 对象，用于解析 Spring Bean
     * @param expr 要执行的 SpEL 表达式字符串
     * @param clazz 返回值的类型 Class 对象
     * @return 表达式计算结果，类型为 T
     */
    fun <T> eval(pjp: ProceedingJoinPoint, beanFactory: BeanFactory, expr: String, clazz: Class<T>): T {
        require(expr.isNotBlank()) { "Expression must not be blank" }

        val method = (pjp.signature as MethodSignature).method
        val ctx = StandardEvaluationContext().apply {
            beanResolver = BeanFactoryResolver(beanFactory)
            setVariable("args", pjp.args)
        }

        val paramNames = names.getParameterNames(method) ?: emptyArray()
        check(paramNames.size == pjp.args.size) { "Parameter names and args size mismatch" }
        if (paramNames.isNotEmpty()) {
            paramNames.forEachIndexed { i, n ->
                ctx.setVariable(n, pjp.args[i])
            }
        }
        return try {
            parser.parseExpression(expr).getValue(ctx, clazz)
        } catch (e: Exception) {
            throw RuntimeException("Failed to evaluate SpEL expression: $expr", e)
        }
    }
}
