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

package com.gewuyou.forgeboot.storage.autoconfigure.selector

import com.gewuyou.forgeboot.storage.api.component.FullFileStorageComponent
import com.gewuyou.forgeboot.storage.api.component.SimpleFileStorageComponent
import com.gewuyou.forgeboot.storage.api.config.StorageProperties
import com.gewuyou.forgeboot.storage.api.factory.StorageComponentFactory

/**
 * 存储组件选择器
 *
 * 根据配置的存储模式自动选择对应的存储实现
 *
 * @since 2025-10-08
 * @author gewuyou
 */
class StorageComponentSelector(
    private val storageProperties: StorageProperties,
    private val factories: List<StorageComponentFactory>,
) {

    /**
     * 选择并创建简单文件存储组件
     */
    fun selectSimpleStorageComponent(): SimpleFileStorageComponent {
        val factory = selectFactory()
        return factory.createSimpleStorageComponent()
    }

    /**
     * 选择并创建完整文件存储组件
     */
    fun selectFullStorageComponent(): FullFileStorageComponent {
        val factory = selectFactory()
        return factory.createFullStorageComponent()
    }

    /**
     * 根据配置的mode选择对应的工厂
     */
    private fun selectFactory(): StorageComponentFactory {
        val mode = storageProperties.mode.lowercase()

        val factory = factories.firstOrNull { it.getMode() == mode && it.isSupported() }
            ?: throw IllegalStateException(
                "No supported storage factory found for mode: $mode. " +
                        "Available modes: ${factories.joinToString { it.getMode() }}"
            )

        return factory
    }

    /**
     * 获取当前选中的存储模式
     */
    fun getCurrentMode(): String = storageProperties.mode

    /**
     * 获取所有可用的存储模式
     */
    fun getAvailableModes(): List<String> = factories.map { it.getMode() }
}