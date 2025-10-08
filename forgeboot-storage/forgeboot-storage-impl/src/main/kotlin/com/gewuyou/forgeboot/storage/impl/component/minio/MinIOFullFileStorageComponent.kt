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

package com.gewuyou.forgeboot.storage.impl.component.minio

import com.gewuyou.forgeboot.core.extension.tryOrFallBack
import com.gewuyou.forgeboot.core.extension.wrapWith
import com.gewuyou.forgeboot.storage.api.component.FullFileStorageComponent
import com.gewuyou.forgeboot.storage.api.config.StorageProperties
import com.gewuyou.forgeboot.storage.api.exception.StorageException
import com.gewuyou.forgeboot.storage.api.generator.FilenameGenerator
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.http.Method
import java.net.URI
import java.time.Instant
import java.util.concurrent.TimeUnit

/**
 *min IO 全文件存储组件
 *
 * @since 2025-10-08 13:04:43
 * @author gewuyou
 */
class MinIOFullFileStorageComponent(
    private val minioClient: MinioClient,
    private val storageProperties: StorageProperties,
    filenameGenerator: FilenameGenerator,
) : MinIOSimpleFileStorageComponent(
    minioClient,
    storageProperties,
    filenameGenerator
), FullFileStorageComponent {
    /**
     * 生成上传预签名URL
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expires 过期时间（秒），默认使用配置的上传预签名有效期
     * @return 上传预签名URL
     */
    override fun generateUploadUrl(
        bucketName: String,
        objectName: String,
        expires: Int,
    ): String {
        return try {
            minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(bucketName)
                    .`object`(objectName)
                    .expiry(expires, TimeUnit.SECONDS)
                    .build()
            )
        } catch (e: Exception) {
            throw StorageException("Failed to generate upload URL for $objectName in bucket $bucketName", e)
        }
    }

    /**
     * 生成上传预签名URL（使用默认过期时间）
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 上传预签名URL
     */
    override fun generateUploadUrl(bucketName: String, objectName: String): String {
        return generateUploadUrl(bucketName, objectName, storageProperties.presignUploadTtl.seconds.toInt())
    }

    /**
     * 生成下载预签名URL
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expires 过期时间（秒），默认使用配置的下载预签名有效期
     * @return 下载预签名URL
     */
    override fun generateDownloadUrl(
        bucketName: String,
        objectName: String,
        expires: Int,
    ): String {
        return {
            minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .`object`(objectName)
                    .expiry(expires, TimeUnit.SECONDS)
                    .build()
            )
        }.wrapWith(
            "Failed to generate download URL for $objectName in bucket $bucketName",
            ::StorageException
        )
    }

    /**
     * 生成下载预签名URL（使用默认过期时间）
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 下载预签名URL
     */
    override fun generateDownloadUrl(bucketName: String, objectName: String): String {
        return generateDownloadUrl(bucketName, objectName, storageProperties.presignDownloadTtl.seconds.toInt())
    }

    /**
     * 验证预签名URL的有效性
     * @param presignUrl 预签名URL
     * @return 有效返回true，否则返回false
     */
    override fun validatePresignUrl(presignUrl: String): Boolean = tryOrFallBack(false) {
        val url = URI(presignUrl).toURL()
        // 从URL中提取过期时间参数
        val query = url.query ?: return false
        val params = query.split("&").associate {
            val (key, value) = it.split("=", limit = 2)
            key to value
        }

        // 检查是否包含MinIO的签名参数
        val expiresParam = params["X-Amz-Expires"] ?: return false
        val dateParam = params["X-Amz-Date"] ?: return false

        // 解析过期时间
        val expiresSeconds = expiresParam.toLongOrNull() ?: return false

        // 解析签名时间 (格式: 20240101T000000Z)
        val signTime = try {
            val year = dateParam.take(4).toInt()
            val month = dateParam.substring(4, 6).toInt()
            val day = dateParam.substring(6, 8).toInt()
            val hour = dateParam.substring(9, 11).toInt()
            val minute = dateParam.substring(11, 13).toInt()
            val second = dateParam.substring(13, 15).toInt()

            java.time.LocalDateTime.of(year, month, day, hour, minute, second)
                .atZone(java.time.ZoneId.of("UTC"))
                .toInstant()
        } catch (_: Exception) {
            return false
        }

        // 检查是否过期
        val expiryTime = signTime.plusSeconds(expiresSeconds)
        Instant.now().isBefore(expiryTime)
    }


    /**
     * 刷新预签名URL（延长有效期）
     * @param presignUrl 原预签名URL
     * @param newExpires 新的过期时间（秒）
     * @return 刷新后的预签名URL
     */
    override fun refreshPresignUrl(presignUrl: String, newExpires: Int): String {
        return {
            val url = URI(presignUrl).toURL()
            // 从URL路径中提取bucket和object信息
            val pathParts = url.path.trimStart('/').split("/", limit = 2)
            if (pathParts.size < 2) {
                throw StorageException("Invalid presigned URL format")
            }

            val bucketName = pathParts[0]
            val objectName = pathParts[1]

            // 根据URL中的method参数判断是上传还是下载URL
            val query = url.query ?: ""
            val isUpload = query.contains("X-Amz-Credential") && presignUrl.contains("PUT")

            // 重新生成预签名URL
            if (isUpload) {
                generateUploadUrl(bucketName, objectName, newExpires)
            } else {
                generateDownloadUrl(bucketName, objectName, newExpires)
            }
        }.wrapWith("Failed to refresh presigned URL", ::StorageException)
    }
}