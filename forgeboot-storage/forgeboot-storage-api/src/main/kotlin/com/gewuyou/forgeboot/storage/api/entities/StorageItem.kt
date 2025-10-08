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

package com.gewuyou.forgeboot.storage.api.entities

/**
 * 存储项
 *
 * @property name 对象名称
 * @property size 对象大小（字节）
 * @property lastModified 最后修改时间
 * @property contentType 内容类型
 * @property etag 对象ETag
 * @since 2025-10-08 09:24:53
 * @author gewuyou
 */
data class StorageItem(
    val name: String,
    val size: Long,
    val lastModified: java.time.Instant,
    val contentType: String? = null,
    val etag: String? = null,
)