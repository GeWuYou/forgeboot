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

package com.gewuyou.forgeboot.storage.api.generator

import com.gewuyou.forgeboot.storage.api.entities.FileHashes
import java.io.InputStream

/**
 * 文件哈希生成器
 *
 * @since 2025-10-08 12:12:39
 * @author gewuyou
 */
interface FileHashGenerator {

    /**
     * 生成文件的MD5哈希值
     *
     * @param inputStream 文件输入流
     * @return MD5哈希值的十六进制字符串
     */
    fun generateMD5(inputStream: InputStream): String

    /**
     * 生成文件的SHA256哈希值
     *
     * @param inputStream 文件输入流
     * @return SHA256哈希值的十六进制字符串
     */
    fun generateSHA256(inputStream: InputStream): String

    /**
     * 同时生成文件的MD5和SHA256哈希值
     *
     * @param inputStream 文件输入流
     * @return 包含MD5和SHA256哈希值的数据类
     */
    fun generateHashes(inputStream: InputStream): FileHashes
}

