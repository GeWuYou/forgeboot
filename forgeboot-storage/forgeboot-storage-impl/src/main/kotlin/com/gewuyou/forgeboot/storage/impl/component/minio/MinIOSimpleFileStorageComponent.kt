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

import com.gewuyou.forgeboot.core.extension.wrapWith
import com.gewuyou.forgeboot.storage.api.component.SimpleFileStorageComponent
import com.gewuyou.forgeboot.storage.api.config.StorageProperties
import com.gewuyou.forgeboot.storage.api.entities.StorageBucket
import com.gewuyou.forgeboot.storage.api.entities.StorageItem
import com.gewuyou.forgeboot.storage.api.entities.meta.UploadMeta
import com.gewuyou.forgeboot.storage.api.exception.StorageException
import com.gewuyou.forgeboot.storage.api.generator.FilenameGenerator
import com.gewuyou.forgeboot.storage.impl.extensions.toStorageBucket
import io.minio.*
import io.minio.http.Method
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

/**
 *min IO桶管理组件
 *
 * @since 2025-10-08 10:54:51
 * @author gewuyou
 */
open class MinIOSimpleFileStorageComponent(
    private val minioClient: MinioClient,
    private val storageProperties: StorageProperties,
    private val filenameGenerator: FilenameGenerator,
) : SimpleFileStorageComponent {
    /**
     * 判断指定名称的桶是否存在。
     *
     * @param bucketName 桶名称
     * @return 存在返回 true，否则返回 false
     */
    override fun bucketExist(bucketName: String): Boolean {
        return try {
            minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())
        } catch (_: Exception) {
            false
        }
    }

    /**
     * 创建一个新的桶。
     *
     * @param bucketName 要创建的桶名称
     */
    override fun createBucket(bucketName: String) {
        { minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build()) }.wrapWith(
            "Failed to create bucket $bucketName",
            exceptionFactory = ::StorageException
        )
    }

    /**
     * 删除一个已存在的桶。
     *
     * @param bucketName 要删除的桶名称
     */
    override fun deleteBucket(bucketName: String) {
        { minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build()) }
            .wrapWith(
                "Failed to delete bucket $bucketName",
                exceptionFactory = ::StorageException
            )
    }

    /**
     * 获取所有桶列表。
     *
     * @return 所有桶的信息列表
     */
    override fun listBucket(): List<StorageBucket> {
        return try {
            val buckets = minioClient.listBuckets(ListBucketsArgs.builder().build())
            buckets.map { result -> result.get().toStorageBucket() }
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * 上传一个文件到默认桶中。
     *
     * @param file 待上传的文件对象
     * @return 返回上传后的文件元信息
     */
    override fun uploadFile(file: MultipartFile): UploadMeta {
        return uploadFile(file, storageProperties.bucket)
    }

    /**
     * 将文件上传到指定桶中。
     *
     * @param file 待上传的文件对象
     * @param bucketName 目标桶名称
     * @return 返回上传后的文件元信息
     */
    override fun uploadFile(
        file: MultipartFile,
        bucketName: String,
    ): UploadMeta {
        // 检查文件是否为空
        if (file.isEmpty) {
            throw StorageException("File is empty")
        }
        // 创建桶
        ensureBucketExists(bucketName)
        val originalFilename = file.originalFilename?.takeIf { it.isNotBlank() }
            ?: "unnamed_${System.currentTimeMillis()}.${file.contentType?.substringAfter("/") ?: "bin"}"
        val fileName = filenameGenerator.generateFilename(originalFilename, storageProperties.filenamePattern)
        val response = {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(fileName)
                    .stream(file.inputStream, file.size, -1)
                    .contentType(file.contentType)
                    .build()
            )
        }.wrapWith(
            "Failed to upload file $originalFilename to bucket $bucketName",
            ::StorageException
        )
        return UploadMeta(
            originalFileName = originalFilename,
            contentType = file.contentType,
            size = file.size,
            storageKey = response.`object`(),
            bucket = bucketName,
            fileName = fileName
        )
    }

    /**
     * 下载指定桶中的某个文件。
     *
     * @param bucketName 桶名称
     * @param objectName 对象名（即文件名）
     * @return 文件输入流
     */
    override fun downloadFile(bucketName: String, objectName: String): InputStream {
        return {
            minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(objectName)
                    .build()
            )
        }.wrapWith(
            "Failed to download file $objectName from bucket $bucketName",
            ::StorageException
        )
    }

    /**
     * 从默认桶中下载指定文件。
     *
     * @param objectName 对象名（即文件名）
     * @return 文件输入流
     */
    override fun downloadFile(objectName: String): InputStream {
        return downloadFile(resolveBucketName(), objectName)
    }

    /**
     * 删除指定桶中的某个文件。
     *
     * @param bucketName 桶名称
     * @param objectName 对象名（即文件名）
     */
    override fun deleteFile(bucketName: String, objectName: String) {
        {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(objectName)
                    .build()
            )
        }.wrapWith(
            "Failed to delete file $objectName from bucket $bucketName",
            ::StorageException
        )
    }

    /**
     * 从默认桶中删除指定文件。
     *
     * @param objectName 对象名（即文件名）
     */
    override fun deleteFile(objectName: String) {
        deleteFile(resolveBucketName(), objectName)
    }

    /**
     * 获取带签名链接的文件访问地址（可设置过期时间）。
     *
     * @param bucketName 桶名称
     * @param objectName 对象名（即文件名）
     * @param expires 过期时间（单位秒），若小于等于0表示永久有效
     * @return 可访问该文件的URL
     */
    override fun getObjectUrl(
        bucketName: String,
        objectName: String,
        expires: Int,
    ): String {
        return {
            if (expires <= 0) {
                // 永久有效链接
                minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .`object`(objectName)
                        .build()
                )
            } else {
                // 有时效的链接
                minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .`object`(objectName)
                        .expiry(expires)
                        .build()
                )
            }
        }.wrapWith(
            "Failed to get object URL for $objectName in bucket $bucketName",
            ::StorageException
        )
    }

    /**
     * 获取带签名链接的文件访问地址（适用于默认桶）。
     *
     * @param objectName 对象名（即文件名）
     * @param expires 过期时间（单位秒），若小于等于0表示永久有效
     * @return 可访问该文件的URL
     */
    override fun getObjectUrl(objectName: String, expires: Int): String {
        return getObjectUrl(resolveBucketName(), objectName, expires)
    }

    /**
     * 判断指定桶中的某个对象是否存在。
     *
     * @param bucketName 桶名称
     * @param objectName 对象名（即文件名）
     * @return 存在返回 true，否则返回 false
     */
    override fun objectExist(bucketName: String, objectName: String): Boolean {
        return try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(objectName)
                    .build()
            )
            true
        } catch (_: Exception) {
            false
        }
    }

    /**
     * 判断默认桶中的某个对象是否存在。
     *
     * @param objectName 对象名（即文件名）
     * @return 存在返回 true，否则返回 false
     */
    override fun objectExist(objectName: String): Boolean {
        return objectExist(resolveBucketName(), objectName)
    }

    /**
     * 在不同桶之间复制一个对象。
     *
     * @param sourceBucket 源桶名称
     * @param sourceObjectName 源对象名
     * @param targetBucket 目标桶名称
     * @param targetObjectName 目标对象名
     */
    override fun copyObject(
        sourceBucket: String,
        sourceObjectName: String,
        targetBucket: String,
        targetObjectName: String,
    ) {
        {
            minioClient.copyObject(
                CopyObjectArgs.builder()
                    .source(
                        CopySource.builder()
                            .bucket(sourceBucket)
                            .`object`(sourceObjectName)
                            .build()
                    )
                    .bucket(targetBucket)
                    .`object`(targetObjectName)
                    .build()
            )
        }.wrapWith(
            "Failed to copy object from $sourceBucket/$sourceObjectName to $targetBucket/$targetObjectName",
            ::StorageException
        )
    }

    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶名称
     */
    fun ensureBucketExists(bucketName: String) {
        // 检查存储桶是否存在，不存在则创建
        if (!bucketExist(bucketName)) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
        }
    }

    /**
     * 列出存储桶中的对象
     *
     * @param bucketName 存储桶名称
     * @param prefix 对象键名前缀，用于过滤结果，默认为空字符串
     * @param recursive 是否递归列出子目录中的对象，默认为false
     * @param maxKeys 最大返回对象数量，默认为1000
     * @return 包含存储项信息的列表
     */
    override fun listObjects(
        bucketName: String,
        prefix: String,
        recursive: Boolean,
        maxKeys: Int,
    ): List<StorageItem> {
        return {
            val results = minioClient.listObjects(
                ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .recursive(recursive)
                    .build()
            )
            // 限制返回数量避免内存溢出
            results.take(maxKeys).map { result ->
                val item = result.get()
                StorageItem(
                    name = item.objectName(),
                    size = item.size(),
                    lastModified = item.lastModified().toInstant(),
                    contentType = null,
                    etag = item.etag()
                )
            }.toList()
        }.wrapWith("Failed to list objects in bucket $bucketName", ::StorageException)
    }

    /**
     * 列出存储桶中的对象
     *
     * @param prefix 对象键名前缀，用于过滤结果，默认为空字符串
     * @param recursive 是否递归列出子目录中的对象，默认为false
     * @param maxKeys 最大返回对象数量，默认为1000
     * @return 包含存储项信息的列表
     */
    override fun listObjects(
        prefix: String,
        recursive: Boolean,
        maxKeys: Int,
    ): List<StorageItem> {
        return listObjects(resolveBucketName(), prefix, recursive, maxKeys)
    }

    /**
     * 解析存储桶名称，如果未提供则使用默认存储桶
     *
     * @param bucketName 存储桶名称，可为空
     * @return 存储桶名称
     */
    private fun resolveBucketName(bucketName: String? = null): String {
        return bucketName ?: storageProperties.bucket
    }
}