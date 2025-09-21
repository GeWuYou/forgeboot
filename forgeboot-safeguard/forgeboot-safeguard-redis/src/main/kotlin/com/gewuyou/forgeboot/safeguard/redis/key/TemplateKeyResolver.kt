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

package com.gewuyou.forgeboot.safeguard.redis.key

import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.key.KeyFactory
import com.gewuyou.forgeboot.safeguard.core.key.KeyResolver

/**
 * 模板键解析器类，用于根据模板名称和上下文变量生成键对象
 *
 * @param C 上下文类型参数
 * @property templateName 模板名称，用于标识键的模板
 * @property factory 键工厂，用于根据模板名称和变量创建键对象
 * @property variables 变量映射函数，将上下文转换为键值对映射
 * @since 2025-09-21 19:19:00
 * @author gewuyou
 */
class TemplateKeyResolver<C>(
    private val templateName: String,
    private val factory: KeyFactory,
    private val variables: (C) -> Map<String, Any?>,
) : KeyResolver<C> {

    /**
     * 解析上下文并生成对应的键对象
     *
     * @param context 上下文对象，用于提取变量值
     * @return 根据模板名称和上下文变量生成的键对象
     */
    override fun resolve(context: C): Key =
        factory.from(templateName, variables(context))
}
