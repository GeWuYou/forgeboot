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

package com.gewuyou.forgeboot.storage.impl.generator

import com.gewuyou.forgeboot.storage.api.generator.FilenameGenerator
import org.springframework.stereotype.Component
import java.util.*

/**
 * 默认文件名生成器
 *
 * @since 2025-10-08 10:51:42
 * @author gewuyou
 */
@Component
class DefaultFilenameGenerator : FilenameGenerator {

    /**
     * 根据给定的原始文件名和模式生成新的文件名
     *
     * @param originalFilename 原始文件名
     * @param pattern 文件名模式，支持 {original}、{uuid}、{timestamp}、{extension} 占位符
     * @return 生成的新文件名
     */
    override fun generateFilename(originalFilename: String, pattern: String): String {
        var result = pattern

        // 提取文件扩展名
        val extension = getFileExtension(originalFilename)

        // 替换占位符
        result = result.replace("{original}", getBaseName(originalFilename))
        result = result.replace("{uuid}", UUID.randomUUID().toString())
        result = result.replace("{timestamp}", System.currentTimeMillis().toString())
        // 如果原始模式包含扩展名占位符，则添加扩展名
        return if (result.contains("{extension}")) {
            result.replace("{extension}", extension)
        } else {
            // 否则直接添加扩展名（如果有的话）
            if (extension.isNotEmpty()) {
                "$result.$extension"
            } else {
                result
            }
        }
    }

    /**
     * 获取文件基础名（不包含扩展名）
     *
     * @param filename 完整文件名
     * @return 文件基础名，如果不包含扩展名则返回原文件名
     */
    private fun getBaseName(filename: String): String {
        val dotIndex = filename.lastIndexOf('.')
        return if (dotIndex > 0) filename.take(dotIndex) else filename
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 完整文件名
     * @return 文件扩展名，如果没有扩展名则返回空字符串
     */
    private fun getFileExtension(filename: String): String {
        val dotIndex = filename.lastIndexOf('.')
        return if (dotIndex > 0 && dotIndex < filename.length - 1) {
            filename.substring(dotIndex + 1)
        } else {
            ""
        }
    }
}