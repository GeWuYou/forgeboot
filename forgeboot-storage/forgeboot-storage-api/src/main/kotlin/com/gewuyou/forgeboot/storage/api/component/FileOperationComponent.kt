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

import com.gewuyou.forgeboot.storage.api.entities.meta.UploadMeta
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

/**
 *文件操作组件
 *
 * @since 2025-10-08 09:52:45
 * @author gewuyou
 */
interface FileOperationComponent {

    /**
     * 上传一个文件到默认桶中。
     *
     * @param file 待上传的文件对象
     * @return 返回上传后的文件元信息
     */
    fun uploadFile(file: MultipartFile): UploadMeta

    /**
     * 将文件上传到指定桶中。
     *
     * @param file 待上传的文件对象
     * @param bucketName 目标桶名称
     * @return 返回上传后的文件元信息
     */
    fun uploadFile(file: MultipartFile, bucketName: String): UploadMeta

    /**
     * 批量上传多个文件到默认桶中。
     *
     * @param files 多个待上传的文件对象
     * @return 返回每个文件对应的元信息列表
     */
    fun uploadFiles(files: List<MultipartFile>): List<UploadMeta> = files.map { uploadFile(it) }

    /**
     * 批量将多个文件上传到指定桶中。
     *
     * @param files 多个待上传的文件对象
     * @param bucketName 目标桶名称
     * @return 返回每个文件对应的元信息列表
     */
    fun uploadFiles(files: List<MultipartFile>, bucketName: String): List<UploadMeta> =
        files.map { uploadFile(it, bucketName) }

    /**
     * 下载指定桶中的某个文件。
     *
     * @param bucketName 桶名称
     * @param objectName 对象名（即文件名）
     * @return 文件输入流
     */
    fun downloadFile(bucketName: String, objectName: String): InputStream

    /**
     * 从默认桶中下载指定文件。
     *
     * @param objectName 对象名（即文件名）
     * @return 文件输入流
     */
    fun downloadFile(objectName: String): InputStream

    /**
     * 删除指定桶中的某个文件。
     *
     * @param bucketName 桶名称
     * @param objectName 对象名（即文件名）
     */
    fun deleteFile(bucketName: String, objectName: String)

    /**
     * 从默认桶中删除指定文件。
     *
     * @param objectName 对象名（即文件名）
     */
    fun deleteFile(objectName: String)

    /**
     * 批量删除指定桶中的多个文件。
     *
     * @param bucketName 桶名称
     * @param objectNames 需要删除的对象名集合
     */
    fun deleteFiles(bucketName: String, objectNames: List<String>) = objectNames.forEach { deleteFile(bucketName, it) }

    /**
     * 批量从默认桶中删除多个文件。
     *
     * @param objectNames 需要删除的对象名集合
     */
    fun deleteFiles(objectNames: List<String>) = objectNames.forEach { deleteFile(it) }

    /**
     * 根据桶名称和对象名映射关系批量删除多个文件。
     *
     * @param bucketNameAndObjectNames 映射结构：键是桶名称，值是要删除的对象名列表
     */
    fun deleteFiles(bucketNameAndObjectNames: Map<String, List<String>>) =
        bucketNameAndObjectNames.forEach { (bucketName, objectNames) ->
            deleteFiles(bucketName, objectNames)
        }

}