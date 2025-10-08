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

package com.gewuyou.forgeboot.storage.api.entities.meta

/**
 * 上传元数据
 *
 * @property fileName 文件名
 * @property originalFileName 原始文件名
 * @property contentType 内容类型
 * @property size 文件大小
 * @property storageKey 存储键
 * @property bucket 存储桶
 * @since 2025-10-08 10:37:05
 * @author gewuyou
 */
data class UploadMeta(
    val fileName: String,
    val originalFileName: String,
    val contentType: String?,
    val size: Long,
    val storageKey: String,
    val bucket: String?,
)
