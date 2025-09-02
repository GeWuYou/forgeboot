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

package com.gewuyou.forgeboot.webmvc.dto.api.entities

/**
 *额外的贡献者
 *
 * @since 2025-09-02 13:24:27
 * @author gewuyou
 */
fun interface ExtraContributor {
    /**
     * 用于向目标Map中添加额外的贡献者信息
     *
     * @param target 目标可变Map，用于存储键值对形式的贡献者信息
     */
    fun contribute(target: MutableMap<String, Any?>)
}
