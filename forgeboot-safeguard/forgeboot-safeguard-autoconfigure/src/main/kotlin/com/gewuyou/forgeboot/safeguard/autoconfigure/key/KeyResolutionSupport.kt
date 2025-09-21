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

package com.gewuyou.forgeboot.safeguard.autoconfigure.key

import com.gewuyou.forgeboot.safeguard.autoconfigure.spel.SpelEval
import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.key.KeyFactory
import com.gewuyou.forgeboot.safeguard.core.key.KeyResolver
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.beans.factory.BeanFactory
import org.springframework.core.DefaultParameterNameDiscoverer

/**
 * KeyResolutionSupport 是一个用于解析缓存键（Key）的支撑类。
 * 它支持通过自定义解析器、模板方式或 SpEL 表达式来生成完整的缓存 Key。
 *
 * @property beanFactory 用于获取 Spring 容器中的 Bean 实例
 * @property keyFactory 用于根据模板和变量生成缓存 Key 的工厂类
 * @since 2025-09-21 19:32:04
 * @author gewuyou
 */
class KeyResolutionSupport(
    private val beanFactory: BeanFactory,
    private val keyFactory: KeyFactory,
) {

    /**
     * 根据不同策略解析出完整的缓存 Key。
     *
     * 解析优先级如下：
     * 1. 如果 resolverBean 不为空，则使用指定的 KeyResolver 进行解析；
     * 2. 否则如果 template 不为空，则使用模板 + 默认变量进行解析；
     * 3. 最后回退到使用 SpEL 表达式解析 key 值，并补充默认命名空间。
     *
     * @param pjp 当前方法连接点，用于获取方法参数等上下文信息
     * @param resolverBean 自定义 KeyResolver 的 Bean 名称（可选）
     * @param template 缓存 Key 模板字符串（可选）
     * @param keyExpr SpEL 表达式，用于提取 key 值（当 resolverBean 和 template 都未提供时使用）
     * @param defaultNamespace 默认命名空间，用于 SpEL 方式生成的 Key
     * @return 解析得到的完整缓存 Key 对象
     */
    @Suppress("UNCHECKED_CAST")
    fun resolveForPjp(
        pjp: ProceedingJoinPoint,
        resolverBean: String,
        template: String,
        keyExpr: String,
        defaultNamespace: String,
    ): Key {
        // 1) 使用自定义解析器解析 Key
        if (resolverBean.isNotBlank()) {
            val r = beanFactory.getBean(resolverBean) as KeyResolver<ProceedingJoinPoint>
            return r.resolve(pjp) // 已经是完整 Key（含 namespace）
        }
        // 2) 使用模板方式解析 Key
        if (template.isNotBlank()) {
            return keyFactory.from(template, defaultVars(pjp)) // 已是完整 Key
        }
        // 3) 回退为 SpEL 表达式解析并补充默认命名空间
        val keyStr = SpelEval.eval(pjp, beanFactory, keyExpr, String::class.java)
        return Key(defaultNamespace, keyStr)
    }

    /**
     * 提取方法参数作为默认变量集合。
     *
     * 包括：
     * - 所有命名参数及其值；
     * - args 数组包含所有参数值。
     *
     * @param pjp 当前方法连接点
     * @return 参数名到参数值的映射表
     */
    private fun defaultVars(pjp: ProceedingJoinPoint): Map<String, Any?> {
        val method = (pjp.signature as org.aspectj.lang.reflect.MethodSignature).method
        val pnd = DefaultParameterNameDiscoverer()
        val names = pnd.getParameterNames(method) ?: emptyArray()
        val vars = HashMap<String, Any?>()
        names.forEachIndexed { i, n -> vars[n] = pjp.args[i] }
        vars["args"] = pjp.args
        // 如需额外上下文（IP、userId），可在此从 RequestContextHolder / SecurityContextHolder 补充
        return vars
    }
}
