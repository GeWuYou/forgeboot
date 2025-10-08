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

package com.gewuyou.forgeboot.storage.autoconfigure.config

import com.gewuyou.forgeboot.storage.api.component.FullFileStorageComponent
import com.gewuyou.forgeboot.storage.api.component.SimpleFileStorageComponent
import com.gewuyou.forgeboot.storage.api.config.StorageProperties
import com.gewuyou.forgeboot.storage.api.factory.StorageComponentFactory
import com.gewuyou.forgeboot.storage.api.generator.FilenameGenerator
import com.gewuyou.forgeboot.storage.autoconfigure.selector.StorageComponentSelector
import com.gewuyou.forgeboot.storage.impl.factory.MinIOStorageComponentFactory
import com.gewuyou.forgeboot.storage.impl.generator.DefaultFilenameGenerator
import io.minio.MinioClient
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

/**
 * 存储自动配置类
 *
 * 使用策略模式提供存储组件的自动配置，支持多种存储后端：
 * - 文件名生成器配置
 * - 存储客户端配置（MinIO等）
 * - 存储工厂配置
 * - 存储组件选择器配置
 * - 存储组件配置（Simple和Full模式）
 *
 * @since 2025-10-07
 * @author gewuyou
 */
@AutoConfiguration
@EnableConfigurationProperties(StorageProperties::class)
class StorageAutoConfiguration {

    // ==================== 文件名生成器配置 ====================

    /**
     * 默认文件名生成器Bean
     * 如果用户没有自定义实现，则使用默认实现
     */
    @Bean
    @ConditionalOnMissingBean
    fun filenameGenerator(): FilenameGenerator {
        return DefaultFilenameGenerator()
    }

    // ==================== MinIO客户端配置 ====================

    /**
     * MinIO客户端Bean
     * 仅在配置了endpoint时才创建
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(MinioClient::class)
    @ConditionalOnProperty(prefix = "forgeboot.storage", name = ["endpoint"])
    fun minioClient(properties: StorageProperties): MinioClient {
        val builder = MinioClient.builder()
            .endpoint(properties.endpoint!!)
            .credentials(properties.accessKey, properties.secretKey)
        return builder.build()
    }

    // ==================== 存储工厂配置 ====================

    /**
     * MinIO存储组件工厂Bean
     * 负责创建MinIO相关的存储组件实例
     */
    @Bean
    @ConditionalOnClass(MinioClient::class)
    @ConditionalOnProperty(prefix = "forgeboot.storage", name = ["mode"], havingValue = "minio", matchIfMissing = true)
    fun minioStorageComponentFactory(
        minioClient: MinioClient,
        properties: StorageProperties,
        filenameGenerator: FilenameGenerator,
    ): StorageComponentFactory {
        return MinIOStorageComponentFactory(minioClient, properties, filenameGenerator)
    }

    /**
     * 本地存储组件工厂Bean
     * 负责创建本地文件系统存储组件实例
     */
    @Bean
    @ConditionalOnProperty(prefix = "forgeboot.storage", name = ["mode"], havingValue = "local")
    fun localStorageComponentFactory(
        properties: StorageProperties,
        filenameGenerator: FilenameGenerator,
    ): StorageComponentFactory {
        return com.gewuyou.forgeboot.storage.impl.factory.LocalStorageComponentFactory(properties, filenameGenerator)
    }

    // ==================== 存储组件选择器配置 ====================

    /**
     * 存储组件选择器Bean
     * 根据配置的mode自动选择对应的存储实现
     */
    @Bean
    @ConditionalOnMissingBean
    fun storageComponentSelector(
        properties: StorageProperties,
        factories: List<StorageComponentFactory>,
    ): StorageComponentSelector {
        return StorageComponentSelector(properties, factories)
    }

    // ==================== 存储组件配置 ====================

    /**
     * 简单文件存储组件Bean
     * 通过选择器动态选择具体的存储实现
     */
    @Bean
    @ConditionalOnMissingBean(FullFileStorageComponent::class)
    fun simpleFileStorageComponent(
        selector: StorageComponentSelector,
    ): SimpleFileStorageComponent {
        return selector.selectSimpleStorageComponent()
    }

    /**
     * 完整文件存储组件Bean
     * 通过选择器动态选择具体的存储实现
     * 提供完整的文件存储功能，包括预签名URL等高级特性
     */
    @Bean
    @ConditionalOnMissingBean
    fun fullFileStorageComponent(
        selector: StorageComponentSelector,
    ): FullFileStorageComponent {
        return selector.selectFullStorageComponent()
    }
}