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
 * 密钥模板注册表接口
 * 用于管理密钥模板的注册、获取和遍历操作
 *
 * @since 2025-09-21 19:03:52
 * @author gewuyou
 */
interface KeyTemplateRegistry {
    /**
     * 注册密钥模板
     * 将指定名称的密钥模板注册到注册表中
     *
     * @param name 模板名称，用于唯一标识该模板
     * @param template 要注册的密钥模板对象
     */
    fun register(name: String, template: KeyTemplate)

    /**
     * 获取指定名称的密钥模板
     * 根据模板名称从注册表中查找对应的密钥模板
     *
     * @param name 要查找的模板名称
     * @return 找到的密钥模板对象，如果未找到则返回null
     */
    operator fun get(name: String): KeyTemplate?

    /**
     * 获取所有已注册的密钥模板
     * 返回注册表中所有密钥模板的映射集合
     *
     * @return 包含所有密钥模板的Map集合，键为模板名称，值为对应的密钥模板对象
     */
    fun all(): Map<String, KeyTemplate>
}
