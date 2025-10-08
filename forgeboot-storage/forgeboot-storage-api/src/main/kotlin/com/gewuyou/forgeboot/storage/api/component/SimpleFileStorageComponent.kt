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

package com.gewuyou.forgeboot.storage.api.component

/**
 * 简单文件存储组件
 *
 * 该接口定义了一个简单的文件存储组件，继承了存储桶管理、文件操作和对象管理功能。
 * 用于提供统一的文件存储服务接口，支持基本的文件存储、检索和管理操作。
 *
 * @since 2025-10-08 09:57:36
 * @author gewuyou
 */
interface SimpleFileStorageComponent : BucketManagementComponent, FileOperationComponent, ObjectManagementComponent {
    /**
     * 将源存储桶中的对象剪切到目标存储桶
     *
     * @param sourceBucket 源存储桶名称
     * @param sourceObjectName 源对象名称
     * @param targetBucket 目标存储桶名称
     * @param targetObjectName 目标对象名称
     */
    override fun cutObject(
        sourceBucket: String,
        sourceObjectName: String,
        targetBucket: String,
        targetObjectName: String,
    ) {
        // 先复制对象
        copyObject(sourceBucket, sourceObjectName, targetBucket, targetObjectName)
        // 再删除源对象
        deleteFile(sourceBucket, sourceObjectName)
    }
}
