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

package com.gewuyou.forgeboot.storage.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * 存储属性配置类
 *
 * 用于配置对象存储相关的属性，包括连接信息、默认配置和功能开关等。
 * 通过@ConfigurationProperties注解绑定application.yml中以"forgeboot.storage"为前缀的配置项。
 *
 * @property endpoint 对象存储服务的访问端点URL
 * @property accessKey 访问对象存储的访问密钥ID
 * @property secretKey 访问对象存储的私有密钥
 * @property mode 存储模式，可选值有"local(还未实现)"、"minio"
 * @property bucket 默认存储桶名称，默认为"uploads"
 * @property presignUploadTtl 上传预签名URL的有效期，默认15分钟
 * @property presignDownloadTtl 下载预签名URL的有效期，默认10分钟
 * @property filenamePattern 上传文件名生成规则，支持占位符如{original}、{uuid}、{timestamp}、{extension}
 * @since 2025-10-06 12:42:49
 * @author gewuyou
 */
@ConfigurationProperties(prefix = "forgeboot.storage")
data class StorageProperties(
    var endpoint: String? = null,
    var accessKey: String? = null,
    var secretKey: String? = null,
    var mode: String = "minio",
    var bucket: String = "uploads",
    var presignUploadTtl: Duration = Duration.ofMinutes(15),
    var presignDownloadTtl: Duration = Duration.ofMinutes(10),
    var filenamePattern: String = "{original}_{uuid}_{timestamp}",
)