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

package com.gewuyou.forgeboot.storage.impl.component.local

import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.storage.api.component.SimpleFileStorageComponent
import com.gewuyou.forgeboot.storage.api.config.StorageProperties
import com.gewuyou.forgeboot.storage.api.entities.StorageBucket
import com.gewuyou.forgeboot.storage.api.entities.StorageItem
import com.gewuyou.forgeboot.storage.api.entities.meta.UploadMeta
import com.gewuyou.forgeboot.storage.api.exception.StorageException
import com.gewuyou.forgeboot.storage.api.generator.FilenameGenerator
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.time.Instant
import kotlin.io.path.*

/**
 * 本地文件存储组件 (示例实现)
 *
 * 注意：这是一个简化的示例实现，展示如何扩展新的存储提供商
 * 生产环境使用需要考虑更多因素（权限、并发、性能等）
 *
 * @property storageProperties 存储配置信息
 * @property filenameGenerator 文件名生成器
 * @property basePath 基础存储路径
 * @since 2025-10-08
 * @author gewuyou
 */
open class LocalSimpleFileStorageComponent(
    private val storageProperties: StorageProperties,
    private val filenameGenerator: FilenameGenerator,
    private val basePath: Path = Paths.get(storageProperties.bucket).toAbsolutePath(),
) : SimpleFileStorageComponent {

    init {
        // 确保基础目录存在
        basePath.createDirectories()
    }

    /**
     * 判断指定名称的存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return 如果存储桶存在返回 true，否则返回 false
     */
    override fun bucketExist(bucketName: String): Boolean {
        return resolveBucketPath(bucketName).exists()
    }

    /**
     * 创建一个新的存储桶
     *
     * @param bucketName 要创建的存储桶名称
     */
    override fun createBucket(bucketName: String) {
        val bucketPath = resolveBucketPath(bucketName)
        if (!bucketPath.exists()) {
            bucketPath.createDirectories()
        }
    }

    /**
     * 删除一个已存在的存储桶及其所有内容
     *
     * @param bucketName 要删除的存储桶名称
     */
    override fun deleteBucket(bucketName: String) {
        val bucketPath = resolveBucketPath(bucketName)
        if (bucketPath.exists()) {
            deleteRecursively(bucketPath.toFile())
        }
    }

    /**
     * 递归删除文件或目录
     *
     * @param file 要删除的文件或目录
     */
    private fun deleteRecursively(file: java.io.File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { deleteRecursively(it) }
        }
        if (!file.delete()) {
            log.error("Failed to delete file: ${file.absolutePath}")
        }
    }

    /**
     * 列出所有存储桶
     *
     * @return 存储桶列表
     */
    override fun listBucket(): List<StorageBucket> {
        return basePath.listDirectoryEntries()
            .filter { it.isDirectory() }
            .map { dir ->
                StorageBucket(
                    name = dir.name,
                    creationDate = Instant.ofEpochMilli(dir.getLastModifiedTime().toMillis()),
                    region = null
                )
            }
    }

    /**
     * 将上传的文件保存到默认存储桶中
     *
     * @param file 要上传的文件
     * @return 包含上传元数据的对象
     * @throws StorageException 如果上传失败
     */
    override fun uploadFile(file: MultipartFile): UploadMeta {
        return uploadFile(file, storageProperties.bucket)
    }

    /**
     * 将上传的文件保存到指定存储桶中
     *
     * @param file 要上传的文件
     * @param bucketName 目标存储桶名称
     * @return 包含上传元数据的对象
     * @throws StorageException 如果上传失败
     */
    override fun uploadFile(file: MultipartFile, bucketName: String): UploadMeta {
        if (file.isEmpty) {
            throw StorageException("File is empty")
        }

        // 确保存储桶存在
        if (!bucketExist(bucketName)) {
            createBucket(bucketName)
        }

        val originalFilename = file.originalFilename ?: "unnamed_${System.currentTimeMillis()}"
        val safeOriginalFilename = sanitizeFilename(originalFilename)
        val fileName = filenameGenerator.generateFilename(safeOriginalFilename, storageProperties.filenamePattern)
        val targetPath = resolveBucketPath(bucketName).resolve(fileName)

        try {
            file.inputStream.use { input ->
                Files.copy(input, targetPath, StandardCopyOption.REPLACE_EXISTING)
            }

            return UploadMeta(
                originalFileName = originalFilename,
                contentType = file.contentType,
                size = file.size,
                storageKey = fileName,
                bucket = bucketName,
                fileName = fileName
            )
        } catch (e: Exception) {
            throw StorageException("Failed to upload file: ${e.message}", e)
        }
    }

    /**
     * 清理文件名中的非法字符
     *
     * @param filename 原始文件名
     * @return 清理后的文件名
     */
    private fun sanitizeFilename(filename: String): String {
        return filename.replace(Regex("[^a-zA-Z0-9._-]"), "_")
    }

    /**
     * 从指定存储桶下载文件
     *
     * @param bucketName 存储桶名称
     * @param objectName 文件对象名称
     * @return 文件输入流
     * @throws StorageException 如果文件不存在
     */
    override fun downloadFile(bucketName: String, objectName: String): InputStream {
        val filePath = resolveBucketPath(bucketName).resolve(objectName)
        if (!filePath.exists()) {
            throw StorageException("File not found: $objectName")
        }
        return filePath.inputStream()
    }

    /**
     * 从默认存储桶下载文件
     *
     * @param objectName 文件对象名称
     * @return 文件输入流
     * @throws StorageException 如果文件不存在
     */
    override fun downloadFile(objectName: String): InputStream {
        return downloadFile(storageProperties.bucket, objectName)
    }

    /**
     * 删除指定存储桶中的文件
     *
     * @param bucketName 存储桶名称
     * @param objectName 文件对象名称
     */
    override fun deleteFile(bucketName: String, objectName: String) {
        val filePath = resolveBucketPath(bucketName).resolve(objectName)
        if (filePath.exists()) {
            filePath.deleteExisting()
        }
    }

    /**
     * 删除默认存储桶中的文件
     *
     * @param objectName 文件对象名称
     */
    override fun deleteFile(objectName: String) {
        deleteFile(storageProperties.bucket, objectName)
    }

    /**
     * 获取指定存储桶中文件的访问路径（本地路径）
     *
     * @param bucketName 存储桶名称
     * @param objectName 文件对象名称
     * @param expires 过期时间（秒），本地存储不使用此参数
     * @return 文件的绝对路径字符串
     */
    override fun getObjectUrl(bucketName: String, objectName: String, expires: Int): String {
        // 本地存储返回文件路径
        return resolveBucketPath(bucketName).resolve(objectName).absolutePathString()
    }

    /**
     * 获取默认存储桶中文件的访问路径（本地路径）
     *
     * @param objectName 文件对象名称
     * @param expires 过期时间（秒），本地存储不使用此参数
     * @return 文件的绝对路径字符串
     */
    override fun getObjectUrl(objectName: String, expires: Int): String {
        return getObjectUrl(storageProperties.bucket, objectName, expires)
    }

    /**
     * 检查指定存储桶中的文件是否存在
     *
     * @param bucketName 存储桶名称
     * @param objectName 文件对象名称
     * @return 如果文件存在返回 true，否则返回 false
     */
    override fun objectExist(bucketName: String, objectName: String): Boolean {
        return resolveBucketPath(bucketName).resolve(objectName).exists()
    }

    /**
     * 检查默认存储桶中的文件是否存在
     *
     * @param objectName 文件对象名称
     * @return 如果文件存在返回 true，否则返回 false
     */
    override fun objectExist(objectName: String): Boolean {
        return objectExist(storageProperties.bucket, objectName)
    }

    /**
     * 复制一个文件对象到另一个存储桶
     *
     * @param sourceBucket 源存储桶名称
     * @param sourceObjectName 源文件对象名称
     * @param targetBucket 目标存储桶名称
     * @param targetObjectName 目标文件对象名称
     * @throws StorageException 如果源文件不存在或复制失败
     */
    override fun copyObject(
        sourceBucket: String,
        sourceObjectName: String,
        targetBucket: String,
        targetObjectName: String,
    ) {
        val sourcePath = resolveBucketPath(sourceBucket).resolve(sourceObjectName)
        val targetPath = resolveBucketPath(targetBucket).resolve(targetObjectName)

        if (!sourcePath.exists()) {
            throw StorageException("Source file not found: $sourceObjectName")
        }

        // 确保目标存储桶存在
        if (!bucketExist(targetBucket)) {
            createBucket(targetBucket)
        }

        sourcePath.copyTo(targetPath, overwrite = true)
    }

    /**
     * 列出指定存储桶中的文件对象
     *
     * @param bucketName 存储桶名称
     * @param prefix 文件名前缀过滤条件
     * @param recursive 是否递归列出子目录中的文件
     * @param maxKeys 最大返回数量
     * @return 文件对象列表
     */
    override fun listObjects(
        bucketName: String,
        prefix: String,
        recursive: Boolean,
        maxKeys: Int,
    ): List<StorageItem> {
        val bucketPath = resolveBucketPath(bucketName)
        if (!bucketPath.exists()) {
            return emptyList()
        }

        val files = if (recursive) {
            bucketPath.walk().filter { it.isRegularFile() }
        } else {
            bucketPath.listDirectoryEntries().asSequence().filter { it.isRegularFile() }
        }

        return files
            .filter { it.name.startsWith(prefix) }
            .take(maxKeys)
            .map { file ->
                StorageItem(
                    name = bucketPath.relativize(file).toString(),
                    size = file.fileSize(),
                    lastModified = Instant.ofEpochMilli(file.getLastModifiedTime().toMillis()),
                    contentType = Files.probeContentType(file),
                    etag = null
                )
            }
            .toList()
    }

    /**
     * 列出默认存储桶中的文件对象
     *
     * @param prefix 文件名前缀过滤条件
     * @param recursive 是否递归列出子目录中的文件
     * @param maxKeys 最大返回数量
     * @return 文件对象列表
     */
    override fun listObjects(prefix: String, recursive: Boolean, maxKeys: Int): List<StorageItem> {
        return listObjects(storageProperties.bucket, prefix, recursive, maxKeys)
    }

    /**
     * 解析存储桶对应的文件系统路径
     *
     * @param bucketName 存储桶名称
     * @return 对应的文件系统路径
     */
    private fun resolveBucketPath(bucketName: String): Path {
        return basePath.resolve(bucketName)
    }
}
