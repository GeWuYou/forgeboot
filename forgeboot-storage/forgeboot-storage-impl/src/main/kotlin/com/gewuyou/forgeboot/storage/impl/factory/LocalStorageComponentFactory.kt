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

package com.gewuyou.forgeboot.storage.impl.factory

import com.gewuyou.forgeboot.storage.api.component.FullFileStorageComponent
import com.gewuyou.forgeboot.storage.api.component.SimpleFileStorageComponent
import com.gewuyou.forgeboot.storage.api.config.StorageProperties
import com.gewuyou.forgeboot.storage.api.factory.StorageComponentFactory
import com.gewuyou.forgeboot.storage.api.generator.FilenameGenerator
import com.gewuyou.forgeboot.storage.impl.component.local.LocalSimpleFileStorageComponent

/**
 * 本地存储组件工厂 (示例实现)
 *
 * 展示如何为新的存储提供商创建工厂
 *
 * @since 2025-10-08
 * @author gewuyou
 */
class LocalStorageComponentFactory(
    private val storageProperties: StorageProperties,
    private val filenameGenerator: FilenameGenerator,
) : StorageComponentFactory {

    override fun getMode(): String = "local"

    override fun createSimpleStorageComponent(): SimpleFileStorageComponent {
        return LocalSimpleFileStorageComponent(storageProperties, filenameGenerator)
    }

    override fun createFullStorageComponent(): FullFileStorageComponent {
        // 本地存储暂不支持预签名URL等高级特性
        // 可以返回简单组件的包装类，或抛出异常
        throw UnsupportedOperationException("Local storage does not support full features like presigned URLs")
    }

    override fun isSupported(): Boolean {
        // 本地存储总是可用的
        return true
    }
}