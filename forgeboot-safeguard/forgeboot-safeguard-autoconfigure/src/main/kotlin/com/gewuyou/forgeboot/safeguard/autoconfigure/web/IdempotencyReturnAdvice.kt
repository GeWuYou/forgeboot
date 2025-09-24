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

package com.gewuyou.forgeboot.safeguard.autoconfigure.web

import com.gewuyou.forgeboot.safeguard.core.exception.IdempotencyReturnValueFromRecordException
import com.gewuyou.forgeboot.safeguard.core.serialize.PayloadCodec
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 幂等性返回值处理的全局异常处理器
 * 当捕获到IdempotencyReturnValueFromRecordException异常时，会从记录中提取并返回之前保存的结果
 *
 * @property codec 用于序列化和反序列化响应体的编解码器
 * @since 2025-09-24 10:22:25
 * @author gewuyou
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
class IdempotencyReturnAdvice(
    private val codec: PayloadCodec,
) {

    /**
     * 处理幂等性返回值异常，从记录中提取并返回之前保存的结果
     *
     * @param ex 幂等性返回值异常，包含幂等性记录信息
     * @return 从幂等性记录中解析出的原始方法返回值
     */
    @ExceptionHandler(IdempotencyReturnValueFromRecordException::class)
    fun onIdemReturn(ex: IdempotencyReturnValueFromRecordException): Any? {
        val rec = ex.record
        val bytes = rec.payload ?: return null
        val typeName = rec.payloadType
        if (!typeName.isNullOrBlank()) {
            return codec.deserialize(bytes, typeName)
        }
        // 如果没有类型信息，兜底按 UTF-8 文本/JSON 尝试
        return String(bytes, Charsets.UTF_8)
    }
}
