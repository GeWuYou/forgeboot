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

package com.gewuyou.forgeboot.safeguard.core.key

/**
 *键模板
 *
 * @since 2025-09-21 09:53:56
 * @author gewuyou
 */
class KeyTemplate(
    val namespace: String,
    val pattern: String, // e.g. "{email}:{scene}"
) {
    private val parts: List<Part> = parse(pattern)

    /**
     * 应用变量映射生成键
     *
     * @param vars 变量名到变量值的映射
     * @return 生成的键对象
     */
    fun apply(vars: Map<String, String>): Key =
        Key(namespace, buildString {
            parts.forEach { p ->
                when (p) {
                    is Part.Lit -> append(p.text)
                    is Part.Var -> append(vars[p.name].orEmpty())
                }
            }
        })

    /**
     * 模板片段接口
     */
    private sealed interface Part {
        /**
         * 字面量片段
         * @param text 字面量文本
         */
        data class Lit(val text: String) : Part

        /**
         * 变量片段
         * @param name 变量名
         */
        data class Var(val name: String) : Part
    }

    /**
     * 解析模板字符串为片段列表
     *
     * @param pat 模板字符串，变量用大括号标记，如"{email}:{scene}"
     * @return 解析后的片段列表
     */
    private fun parse(pat: String): List<Part> {
        val out = mutableListOf<Part>()
        val r = Regex("\\{(\\w+)}")
        var last = 0
        r.findAll(pat).forEach { m ->
            // 添加变量前的字面量部分
            if (m.range.first > last) out += Part.Lit(pat.substring(last, m.range.first))
            // 添加变量部分
            out += Part.Var(m.groupValues[1])
            last = m.range.last + 1
        }
        // 添加末尾的字面量部分
        if (last < pat.length) out += Part.Lit(pat.substring(last))
        return out
    }
}
