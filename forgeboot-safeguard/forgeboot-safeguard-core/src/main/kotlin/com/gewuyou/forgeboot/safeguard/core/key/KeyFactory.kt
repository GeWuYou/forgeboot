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
 * KeyFactory类用于根据模板生成Key对象
 *
 * @param registry Key模板注册表，用于存储和查找Key模板
 * @since 2025-09-21 19:07:41
 * @author gewuyou
 */
class KeyFactory(private val registry: KeyTemplateRegistry) {

    /**
     * 根据命名空间、模式和变量生成Key对象
     *
     * @param ns 命名空间字符串
     * @param pattern 模式字符串
     * @param vars 变量映射表，键为变量名，值为变量值
     * @return 生成的Key对象
     */
    fun apply(ns: String, pattern: String, vars: Map<String, Any?>): Key =
        KeyTemplate(ns, pattern).apply(vars.mapValues { it.value?.toString() ?: "" })


    /**
     * 根据模板名称和变量从注册表中查找模板并生成Key对象
     *
     * @param name 模板名称
     * @param vars 变量映射表，键为变量名，值为变量值
     * @return 生成的Key对象
     * @throws IllegalStateException 当找不到指定名称的模板时抛出异常
     */
    fun from(name: String, vars: Map<String, Any?>): Key {
        // 从注册表中查找指定名称的模板，如果找不到则抛出异常
        val t = registry[name]
            ?: error("Key template not found: $name")
        // 应用变量到模板并返回生成的Key对象
        return t.apply(vars.mapValues { it.value?.toString() ?: "" })
    }
}
