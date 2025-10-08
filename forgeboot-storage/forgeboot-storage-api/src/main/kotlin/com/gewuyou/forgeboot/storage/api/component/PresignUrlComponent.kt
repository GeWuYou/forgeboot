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
 *预签名 url 组件
 *
 * @since 2025-10-08 10:32:54
 * @author gewuyou
 */
interface PresignUrlComponent {

    /**
     * 生成上传预签名URL
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expires 过期时间（秒），默认使用配置的上传预签名有效期
     * @return 上传预签名URL
     */
    fun generateUploadUrl(bucketName: String, objectName: String, expires: Int): String

    /**
     * 生成上传预签名URL（使用默认过期时间）
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 上传预签名URL
     */
    fun generateUploadUrl(bucketName: String, objectName: String): String

    /**
     * 生成下载预签名URL
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expires 过期时间（秒），默认使用配置的下载预签名有效期
     * @return 下载预签名URL
     */
    fun generateDownloadUrl(bucketName: String, objectName: String, expires: Int): String

    /**
     * 生成下载预签名URL（使用默认过期时间）
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 下载预签名URL
     */
    fun generateDownloadUrl(bucketName: String, objectName: String): String

    /**
     * 验证预签名URL的有效性
     * @param presignUrl 预签名URL
     * @return 有效返回true，否则返回false
     */
    fun validatePresignUrl(presignUrl: String): Boolean

    /**
     * 刷新预签名URL（延长有效期）
     * @param presignUrl 原预签名URL
     * @param newExpires 新的过期时间（秒）
     * @return 刷新后的预签名URL
     */
    fun refreshPresignUrl(presignUrl: String, newExpires: Int): String
}