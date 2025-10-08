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
import com.gewuyou.forgeboot.storage.impl.component.minio.MinIOFullFileStorageComponent
import com.gewuyou.forgeboot.storage.impl.component.minio.MinIOSimpleFileStorageComponent
import io.minio.MinioClient

/**
 * MinIO 存储组件工厂
 *
 * @since 2025-10-08
 * @author gewuyou
 */
class MinIOStorageComponentFactory(
    private val minioClient: MinioClient,
    private val storageProperties: StorageProperties,
    private val filenameGenerator: FilenameGenerator,
) : StorageComponentFactory {

    override fun getMode(): String = "minio"

    override fun createSimpleStorageComponent(): SimpleFileStorageComponent {
        return MinIOSimpleFileStorageComponent(minioClient, storageProperties, filenameGenerator)
    }

    override fun createFullStorageComponent(): FullFileStorageComponent {
        return MinIOFullFileStorageComponent(minioClient, storageProperties, filenameGenerator)
    }

    override fun isSupported(): Boolean {
        return storageProperties.endpoint != null &&
                storageProperties.accessKey != null &&
                storageProperties.secretKey != null
    }
}