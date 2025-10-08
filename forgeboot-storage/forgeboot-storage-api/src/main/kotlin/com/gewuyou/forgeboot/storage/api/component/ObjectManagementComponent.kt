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

import com.gewuyou.forgeboot.storage.api.entities.StorageItem

/**
 *对象管理组件
 *
 * @since 2025-10-08 09:55:03
 * @author gewuyou
 */
interface ObjectManagementComponent {

    /**
     * 获取带签名链接的文件访问地址（可设置过期时间）。
     *
     * @param bucketName 桶名称
     * @param objectName 对象名（即文件名）
     * @param expires 过期时间（单位秒），若小于等于0表示永久有效
     * @return 可访问该文件的URL
     */
    fun getObjectUrl(bucketName: String, objectName: String, expires: Int): String

    /**
     * 获取带签名链接的文件访问地址，默认永不过期。
     *
     * @param bucketName 桶名称
     * @param objectName 对象名（即文件名）
     * @return 可访问该文件的URL
     */
    fun getObjectUrl(bucketName: String, objectName: String): String = getObjectUrl(bucketName, objectName, -1)

    /**
     * 获取带签名链接的文件访问地址（适用于默认桶）。
     *
     * @param objectName 对象名（即文件名）
     * @param expires 过期时间（单位秒），若小于等于0表示永久有效
     * @return 可访问该文件的URL
     */
    fun getObjectUrl(objectName: String, expires: Int): String

    /**
     * 获取带签名链接的文件访问地址（适用于默认桶且永不过期）。
     *
     * @param objectName 对象名（即文件名）
     * @return 可访问该文件的URL
     */
    fun getObjectUrl(objectName: String): String = getObjectUrl(objectName, -1)

    /**
     * 判断指定桶中的某个对象是否存在。
     *
     * @param bucketName 桶名称
     * @param objectName 对象名（即文件名）
     * @return 存在返回 true，否则返回 false
     */
    fun objectExist(bucketName: String, objectName: String): Boolean

    /**
     * 判断默认桶中的某个对象是否存在。
     *
     * @param objectName 对象名（即文件名）
     * @return 存在返回 true，否则返回 false
     */
    fun objectExist(objectName: String): Boolean


    /**
     * 列出存储桶中的对象
     *
     * @param bucketName 存储桶名称
     * @param prefix 对象键名前缀，用于过滤结果，默认为空字符串
     * @param recursive 是否递归列出子目录中的对象，默认为false
     * @param maxKeys 最大返回对象数量，默认为1000
     * @return 包含存储项信息的列表
     */
    fun listObjects(
        bucketName: String,
        prefix: String = "",
        recursive: Boolean = false,
        maxKeys: Int = 1000,
    ): List<StorageItem>

    /**
     * 列出存储桶中的对象
     *
     * @param prefix 对象键名前缀，用于过滤结果，默认为空字符串
     * @param recursive 是否递归列出子目录中的对象，默认为false
     * @param maxKeys 最大返回对象数量，默认为1000
     * @return 包含存储项信息的列表
     */
    fun listObjects(
        prefix: String = "",
        recursive: Boolean = false,
        maxKeys: Int = 1000,
    ): List<StorageItem>

    /**
     * 在不同桶之间复制一个对象。
     *
     * @param sourceBucket 源桶名称
     * @param sourceObjectName 源对象名
     * @param targetBucket 目标桶名称
     * @param targetObjectName 目标对象名
     */
    fun copyObject(
        sourceBucket: String,
        sourceObjectName: String,
        targetBucket: String,
        targetObjectName: String,
    )

    /**
     * 将源存储桶中的对象剪切到目标存储桶
     *
     * @param sourceBucket 源存储桶名称
     * @param sourceObjectName 源对象名称
     * @param targetBucket 目标存储桶名称
     * @param targetObjectName 目标对象名称
     */
    fun cutObject(
        sourceBucket: String,
        sourceObjectName: String,
        targetBucket: String,
        targetObjectName: String,
    )

}