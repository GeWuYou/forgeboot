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

package com.gewuyou.forgeboot.storage.api.factory

import com.gewuyou.forgeboot.storage.api.component.FullFileStorageComponent
import com.gewuyou.forgeboot.storage.api.component.SimpleFileStorageComponent

/**
 * 存储组件工厂接口
 *
 * 用于根据配置的存储模式创建对应的存储组件实例
 *
 * @since 2025-10-08
 * @author gewuyou
 */
interface StorageComponentFactory {

    /**
     * 获取存储模式标识
     * 例如: "minio", "oss", "cos", "local"
     */
    fun getMode(): String

    /**
     * 创建简单文件存储组件
     */
    fun createSimpleStorageComponent(): SimpleFileStorageComponent

    /**
     * 创建完整文件存储组件
     */
    fun createFullStorageComponent(): FullFileStorageComponent

    /**
     * 检查是否支持当前配置
     */
    fun isSupported(): Boolean = true
}